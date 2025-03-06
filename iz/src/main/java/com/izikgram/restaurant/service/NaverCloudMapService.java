package com.izikgram.restaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izikgram.restaurant.entity.Restaurant;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.proj4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NaverCloudMapService {
    @Value("${ncp.api.key.id}")
    private String apiKeyId;

    @Value("${ncp.api.key}")
    private String apiKey;

    @Value("${naver.area.search.api.key.id}")
    private String NaverapiKeyId;

    @Value("${naver.area.search.api.key}")
    private String NaverapiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 거리 계산 상수
    private static final double EARTH_RADIUS_KM = 6371.0;

    // 내부 좌표 클래스
    private static class LatLng {
        double latitude;
        double longitude;

        public LatLng(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    public List<Restaurant> searchNearbyRestaurants(
            double latitude,
            double longitude,
            String category) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            List<Restaurant> allResults = new ArrayList<>();

            // 역지오코딩으로 현재 위치의 상세 정보 가져오기
            String reverseGeoUrl = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc" +
                    "?coords=" + longitude + "," + latitude +
                    "&output=json" +
                    "&orders=legalcode,admcode,addr,roadaddr";

            HttpHeaders geoHeaders = new HttpHeaders();
            geoHeaders.set("X-NCP-APIGW-API-KEY-ID", apiKeyId);
            geoHeaders.set("X-NCP-APIGW-API-KEY", apiKey);

            HttpEntity<?> geoEntity = new HttpEntity<>(geoHeaders);

            try {
                // 역지오코딩 API 호출
                ResponseEntity<String> geoResponse = restTemplate.exchange(
                        reverseGeoUrl,
                        HttpMethod.GET,
                        geoEntity,
                        String.class
                );

                // 상세 지역 정보 파싱
                String locationQuery = extractLocationFromGeoResponse(geoResponse.getBody());
                log.info("추출된 상세 위치 정보: {}", locationQuery);

                // 지역명 추출 - 더 상세한 정보를 포함하는 경우에도 구와 동은 여전히 첫 번째와 두 번째 단어
                String[] locationParts = locationQuery.split("\\s+");
                String district = locationParts.length > 0 ? locationParts[0] : ""; // 첫 번째 단어 (구 이름)
                String neighborhood = locationParts.length > 1 ? locationParts[1] : ""; // 두 번째 단어 (동 이름)

                // 세 번째 단어부터는 상세주소 (번지 또는 도로명)
                StringBuilder detailAddress = new StringBuilder();
                for (int i = 2; i < locationParts.length; i++) {
                    detailAddress.append(locationParts[i]).append(" ");
                }
                String detailInfo = detailAddress.toString().trim();

                HttpHeaders searchHeaders = new HttpHeaders();
                searchHeaders.set("X-Naver-Client-Id", NaverapiKeyId);
                searchHeaders.set("X-Naver-Client-Secret", NaverapiKey);
                HttpEntity<?> searchEntity = new HttpEntity<>(searchHeaders);

                // 선택된 카테고리와 상세 위치를 기반으로 검색 쿼리 설정
                // 여기에 상세 위치 정보(번지, 도로명 등)를 활용할 수 있음
                List<String> searchQueries = buildSearchQueries(district, neighborhood, category);

                // 더 정확한 위치 검색을 위해 상세 정보가 있으면 추가 검색 쿼리 생성
                if (!detailInfo.isEmpty()) {
                    searchQueries.addAll(buildDetailedSearchQueries(district, neighborhood, detailInfo, category));
                }

                // 중복 방지를 위한 Set
                Set<String> processedTitles = new HashSet<>();

                for (String query : searchQueries) {
                    // 1. 텍스트 기반 검색(역삼동 테헤란로 OOO)
                    URI textUri = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/search/local.json")
                            .queryParam("query", query)
                            .queryParam("display", 100)  // 최대 결과 요청
                            .queryParam("sort", "random")
                            .build()
                            .encode()
                            .toUri();

                    try {
                        ResponseEntity<String> textResponse = restTemplate.exchange(
                                textUri,
                                HttpMethod.GET,
                                searchEntity,
                                String.class
                        );

                        List<Restaurant> textResults = processSearchResults(
                                textResponse.getBody(), latitude, longitude, "텍스트 검색: " + query);

                        // 중복 제거하면서 결과 추가
                        for (Restaurant restaurant : textResults) {
                            if (!processedTitles.contains(restaurant.getTitle())) {
                                allResults.add(restaurant);
                                processedTitles.add(restaurant.getTitle());
                            }
                        }
                    } catch (Exception e) {
                        log.error("텍스트 검색 API 호출 중 오류: {} - {}", query, e.getMessage());
                    }

                    // 2. 위치 기반 검색 (단순 카테고리 검색)
                    if (!query.contains(district)) {
                        URI locationUri = UriComponentsBuilder.fromHttpUrl("https://openapi.naver.com/v1/search/local.json")
                                .queryParam("query", query)
                                .queryParam("display", 100)
                                .queryParam("x", longitude)
                                .queryParam("y", latitude)
                                .build()
                                .encode()
                                .toUri();

                        try {
                            ResponseEntity<String> locationResponse = restTemplate.exchange(
                                    locationUri,
                                    HttpMethod.GET,
                                    searchEntity,
                                    String.class
                            );

                            List<Restaurant> locationResults = processSearchResults(
                                    locationResponse.getBody(), latitude, longitude, "위치 기반 검색: " + query);

                            // 중복 제거하면서 결과 추가
                            for (Restaurant restaurant : locationResults) {
                                if (!processedTitles.contains(restaurant.getTitle())) {
                                    allResults.add(restaurant);
                                    processedTitles.add(restaurant.getTitle());
                                }
                            }
                        } catch (Exception e) {
                            log.error("위치 기반 검색 API 호출 중 오류: {} - {}", query, e.getMessage());
                        }
                    }
                }

                // 거리 기반 필터링 및 정렬
                List<Restaurant> filteredResults = allResults.stream()
                        .filter(r -> {
                            // 주소에 현재 위치 정보(구 이름)가 포함되어 있는지 확인
                            boolean isInSameDistrict = false;
                            if (r.getAddress() != null) {
                                isInSameDistrict = r.getAddress().contains(district);
                            }
                            if (!isInSameDistrict && r.getRoadAddress() != null) {
                                isInSameDistrict = r.getRoadAddress().contains(district);
                            }

                            // 카테고리 확인 (전체 검색인 경우 모든 음식 관련 카테고리 포함)
                            boolean isCategoryMatch = isCategoryMatch(r.getCategory(), category);

                            // 거리가 설정한 반경의 2배 이내이거나 같은 구에 있는 음식점만 포함
                            return isInSameDistrict && isCategoryMatch;
                        })
                        .sorted(Comparator.comparingInt(Restaurant::getDistance))
                        .collect(Collectors.toList());

                // 최대 100개 결과로 제한
                if (filteredResults.size() > 100) {
                    filteredResults = filteredResults.subList(0, 100);
                }

                return filteredResults;

            } catch (Exception e) {
                log.error("API 호출 중 오류 발생: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("처리 중 예상치 못한 오류: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // 상세 위치 정보를 활용한 추가 검색 쿼리 생성 메서드
    private List<String> buildDetailedSearchQueries(String district, String neighborhood, String detailInfo, String category) {
        List<String> queries = new ArrayList<>();

        // 도로명 주소 형식이면 도로명만 추출 (괄호 안에 있는 도로명)
        String roadName = "";
        if (detailInfo.contains("(") && detailInfo.contains(")")) {
            int startIndex = detailInfo.indexOf("(") + 1;
            int endIndex = detailInfo.indexOf(")");
            if (startIndex < endIndex) {
                roadName = detailInfo.substring(startIndex, endIndex);
                // 도로명에서 번호만 제거하고 도로명만 남김
                roadName = roadName.replaceAll("\\d+-?\\d*", "").trim();
            }
        }

        // 카테고리와 상세 위치를 조합한 검색어 생성
        if (!category.equals("전체") && !category.isEmpty()) {
            if (!roadName.isEmpty()) {
                queries.add(district + " " + roadName + " " + category);
                queries.add(roadName + " " + category);
            }

            // 동 + 번지 + 카테고리
            if (!neighborhood.isEmpty() && detailInfo.matches(".*\\d+.*")) {
                queries.add(neighborhood + " " + detailInfo.replaceAll("[()]", "") + " " + category);
            }
        } else {
            // 전체 카테고리 검색인 경우 위치 기반으로만 검색
            if (!roadName.isEmpty()) {
                queries.add(district + " " + roadName + " 맛집");
                queries.add(roadName + " 음식점");
            }

            if (!neighborhood.isEmpty() && detailInfo.matches(".*\\d+.*")) {
                queries.add(neighborhood + " " + detailInfo.replaceAll("[()]", "") + " 맛집");
            }
        }

        return queries;
    }

    private List<String> buildSearchQueries(String district, String neighborhood, String category) {
        List<String> queries = new ArrayList<>();

        if (!"전체".equals(category)) {
            // 특정 카테고리 선택 시 다양한 쿼리 생성
            queries.add(district + " " + neighborhood + " " + category);
            queries.add(district + " " + category);
            queries.add(neighborhood + " " + category);
            queries.add(category); // 단순 카테고리 검색
        } else {
            // 전체/맛집 카테고리의 경우 더 광범위한 검색
            queries.add(district + " " + neighborhood + " 맛집");
            queries.add(district + " " + neighborhood + " 한식");
            queries.add(district + " " + neighborhood + " 중식");
            queries.add(district + " " + neighborhood + " 양식");
            queries.add(district + " " + neighborhood + " 일식");
            queries.add(district + " " + neighborhood + " 햄버거");
            queries.add(district + " " + neighborhood + " 쌀국수");
            queries.add(district + " " + neighborhood + " 베트남음식");
            queries.add(district + " " + neighborhood + " 치킨");
            queries.add(district + " " + neighborhood + " 피자");
        }

        return queries;
    }

    // 카테고리 매칭 확인
    private boolean isCategoryMatch(String categoryStr, String selectedCategory) {
        if (categoryStr == null) return false;

        String lowerCategory = categoryStr.toLowerCase();

        // 전체 선택 시 모든 음식 관련 카테고리 포함
        if ("전체".equals(selectedCategory)) {
            return lowerCategory.contains("음식점") ||
                    lowerCategory.contains("식당") ||
                    lowerCategory.contains("카페") ||
                    lowerCategory.contains("햄버거") ||
                    lowerCategory.contains("베이커리") ||
                    lowerCategory.contains("한식") ||
                    lowerCategory.contains("일식") ||
                    lowerCategory.contains("중식") ||
                    lowerCategory.contains("양식") ||
                    lowerCategory.contains("분식");
        }

        // 특정 카테고리 선택 시 해당 카테고리만 포함
        return lowerCategory.contains(selectedCategory.toLowerCase());
    }

    // 검색 결과를 처리하는 헬퍼 메소드
    private List<Restaurant> processSearchResults(
            String responseBody,
            double latitude,
            double longitude,
            String searchType) {

        try {

            // JSON 응답 파싱
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode itemsNode = rootNode.get("items");
            int totalCount = rootNode.has("total") ? rootNode.get("total").asInt() : 0;

            if (itemsNode == null || !itemsNode.isArray() || itemsNode.size() == 0) {
                return Collections.emptyList();
            }

            // 음식점 리스트 생성
            List<Restaurant> restaurants = new ArrayList<>();
            for (JsonNode item : itemsNode) {
                Restaurant restaurant = new Restaurant();
                restaurant.setTitle(getTextValue(item, "title"));
                restaurant.setCategory(getTextValue(item, "category"));
                restaurant.setDescription(getTextValue(item, "description"));
                restaurant.setTelephone(getTextValue(item, "telephone"));
                restaurant.setAddress(getTextValue(item, "address"));
                restaurant.setRoadAddress(getTextValue(item, "roadAddress"));
                restaurant.setMapx(getTextValue(item, "mapx"));
                restaurant.setMapy(getTextValue(item, "mapy"));
                restaurant.setLink(getTextValue(item, "link"));

                // 거리 처리
                int distance = processDistance(restaurant, latitude, longitude);
                restaurant.setDistance(distance);

//                // 장소 로깅 (디버깅 정보가 너무 많아지지 않도록 일부만 로깅)
//                if (restaurants.size() < 5) {
//                    log.info("{} 결과 - 음식점: {}, 거리: {}m, 카테고리: {}, 주소: {}",
//                            searchType,
//                            restaurant.getTitle(),
//                            restaurant.getDistance(),
//                            restaurant.getCategory(),
//                            restaurant.getRoadAddress() != null ? restaurant.getRoadAddress() : restaurant.getAddress());
//                }

                restaurants.add(restaurant);
            }
            return restaurants;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private int processDistance(Restaurant restaurant, double userLat, double userLon) {
        try {
            // TM128 좌표 처리 (우선)
            String mapx = restaurant.getMapx();
            String mapy = restaurant.getMapy();

            if (mapx != null && !mapx.isEmpty() && mapy != null && !mapy.isEmpty()) {
//                log.info("식당 '{}' 좌표 처리: mapx={}, mapy={}", restaurant.getTitle(), mapx, mapy);

                // 직접 변환 시도
                LatLng wgs84Coords = naverTM128ToWGS84(mapx, mapy);

                if (wgs84Coords != null) {
                    // 변환된 좌표 로깅
//                    log.info("변환된 WGS84 좌표: 위도={}, 경도={}",
//                            wgs84Coords.latitude, wgs84Coords.longitude);

                    // 거리 계산
                    int calculatedDistance = calculateDistance(
                            userLat, userLon,
                            wgs84Coords.latitude, wgs84Coords.longitude);

//                    log.info("계산된 거리: {}m", calculatedDistance);

                    // 합리적인 거리인지 확인 (너무 멀면 변환 오류)
                    if (calculatedDistance >= 0 && calculatedDistance < 10000) {
                        return calculatedDistance;
                    } else {
                        log.warn("비합리적인 거리 계산됨: {}m, 좌표 변환 문제 의심", calculatedDistance);
                    }
                } else {
                    log.warn("좌표 변환 실패: mapx={}, mapy={}", mapx, mapy);
                }
            }

            // 주소 기반 거리 계산은 현재 지오코딩 API 접근 권한 문제로 작동하지 않음
            // 따라서 임의의 거리 값을 반환 (카테고리와 업체명 기반)
            String category = restaurant.getCategory() != null ? restaurant.getCategory() : "";
            String title = restaurant.getTitle() != null ? restaurant.getTitle() : "";

            // 카테고리와 제목에 기반한 가중치 적용 임시 거리 계산
            // 실제 거리가 아닌 순서만 의미있는 값임
            int baseDistance = 500;
            if (category.contains("한식")) baseDistance -= 100;
            if (category.contains("중식")) baseDistance -= 50;
            if (category.contains("일식")) baseDistance -= 80;
            if (category.contains("양식")) baseDistance -= 70;
            if (title.contains("맛집")) baseDistance -= 120;

            // 무작위성 추가 (동일 카테고리 내 순서 섞기)
            baseDistance += (title.length() % 100);

//            log.info("임시 거리 값 할당: {}m, 업체: {}, 카테고리: {}", baseDistance, title, category);
            return baseDistance;
        } catch (Exception e) {
            log.error("거리 계산 중 오류: {}", e.getMessage(), e);
            // 실패 시 기본 값
            return 999;
        }
    }

    // 네이버 지도 API에서 사용하는 TM128 좌표를 WGS84 좌표로 변환합니다.네이버에서 제공하는 공식적인 변환 공식을 사용합니다.
    private LatLng naverTM128ToWGS84(String mapx, String mapy) {
        try {
            double x = Double.parseDouble(mapx);
            double y = Double.parseDouble(mapy);

            // 좌표가 이미 WGS84일 경우 (작은 숫자)
            if (x < 180 && y < 90) {
                return new LatLng(y, x);
            }

            // x, y가 정수형이고 큰 숫자인 경우 - 네이버 특수 형식 (10^7 배율)
            // 예: 1269789145 -> 126.9789145
            if (x > 1000000 && y > 1000000) {
                double lon = x / 10000000.0;
                double lat = y / 10000000.0;

                // 한국 영토 범위 내에 있는지 확인 (대략)
                if (lat >= 33.0 && lat <= 43.0 && lon >= 124.0 && lon <= 132.0) {
//                    log.info("네이버 TM128 특수 형식 변환: lat={}, lon={}", lat, lon);
                    return new LatLng(lat, lon);
                }
            }

//            // 기존 PROJ4J 변환 시도 (TM 좌표계 범위에 있을 경우)
//            if (x >= 100000 && x <= 1000000 && y >= 100000 && y <= 1000000) {
//                // PROJ4J 변환 로직 유지
//                CRSFactory csFactory = new CRSFactory();
//
//                // 네이버 지도 API TM128 좌표계 정의
//                CoordinateReferenceSystem sourceCRS = csFactory.createFromParameters("NAVER_TM128",
//                        "+proj=tmerc +lat_0=38 +lon_0=128 +k=0.9999 +x_0=400000 +y_0=600000 +ellps=bessel +units=m +no_defs");
//
//                // WGS84 좌표계 정의
//                CoordinateReferenceSystem targetCRS = csFactory.createFromName("EPSG:4326");
//
//                // 좌표 변환 설정
//                CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
//                CoordinateTransform transform = ctFactory.createTransform(sourceCRS, targetCRS);
//
//                // 좌표 변환 수행
//                ProjCoordinate sourceCoord = new ProjCoordinate(x, y);
//                ProjCoordinate targetCoord = new ProjCoordinate();
//
//                transform.transform(sourceCoord, targetCoord);
//
//                log.info("PROJ4J 변환 결과: lat={}, lon={}", targetCoord.y, targetCoord.x);
//                return new LatLng(targetCoord.y, targetCoord.x);
//            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // 두 좌표 사이의 거리를 미터 단위로 계산 (Haversine 공식 사용)
    private int calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceKm = EARTH_RADIUS_KM * c;

        return (int) (distanceKm * 1000); // 미터 단위로 변환
    }

    // 역지오코딩 응답에서 위치 정보(동/구) 추출
    private String extractLocationFromGeoResponse(String geoResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(geoResponse);
            JsonNode resultsNode = rootNode.get("results");

            String area2 = "";  // 구/군
            String area3 = "";  // 동
            String area4 = "";  // 번지/통/리
            String roadName = "";  // 도로명

            if (resultsNode != null && resultsNode.isArray() && resultsNode.size() > 0) {
                // 법정동 코드 또는 행정동 코드에서 정보 추출
                for (JsonNode result : resultsNode) {
                    String name = result.path("name").asText();

                    if ("legalcode".equals(name) || "admcode".equals(name) || "addr".equals(name)) {
                        JsonNode region = result.get("region");
                        if (region != null) {
                            // 구/군 정보 추출
                            if (area2.isEmpty()) {
                                area2 = region.path("area2").path("name").asText();
                            }
                            // 동 정보 추출
                            if (area3.isEmpty()) {
                                area3 = region.path("area3").path("name").asText();
                            }
                            // 번지/통/리 정보 추출
                            if (area4.isEmpty()) {
                                area4 = region.path("area4").path("name").asText();
                            }
                        }
                    }
                    // 도로명 주소 정보 추출
                    else if ("roadaddr".equals(name)) {
                        JsonNode land = result.path("land");
                        if (!land.isMissingNode()) {
                            roadName = land.path("name").asText();

                            // 도로명 + 건물번호
                            String number1 = land.path("number1").asText();
                            String number2 = land.path("number2").asText();

                            if (!number1.isEmpty()) {
                                roadName += " " + number1;
                                if (!number2.isEmpty() && !"0".equals(number2)) {
                                    roadName += "-" + number2;
                                }
                            }
                        }
                    }
                }

                // 결과 조합
                StringBuilder locationBuilder = new StringBuilder();

                if (!area2.isEmpty()) {
                    locationBuilder.append(area2);
                }

                if (!area3.isEmpty()) {
                    if (locationBuilder.length() > 0) {
                        locationBuilder.append(" ");
                    }
                    locationBuilder.append(area3);
                }

                // 번지 또는 도로명 중 하나 선택하여 추가
                if (!area4.isEmpty() && !"0".equals(area4)) {
                    locationBuilder.append(" ").append(area4);
                } else if (!roadName.isEmpty()) {
                    locationBuilder.append(" (").append(roadName).append(")");
                }

                return locationBuilder.toString();
            }
        } catch (Exception e) {
            log.error("위치 정보 추출 중 오류: {}", e.getMessage());
        }
        return "";
    }

    // 안전하게 JSON 노드에서 텍스트 값을 추출하는 유틸리티 메서드
    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode != null && fieldNode.isTextual()) {
            String value = fieldNode.asText();
            // HTML 태그 제거
            return value.replaceAll("<[^>]*>", "");
        }
        return "";
    }

}