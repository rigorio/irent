package rigor.io.irent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import rigor.io.irent.house.House;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShimattaTest {

  @Test
  public void filteringTechnique() throws IOException {
    Map<String, Object> filters = new HashMap<>();
//    filters.put("keyword", "street");
//    filters.put("propertyType", "Rooms");
//    filters.put("minPrice", 0);
//    filters.put("maxPrice", 90000);
    filters.put("slots", 5);
//    filters.put("rating", 1);


    List<House> houses = filterHouses(filters);
    System.out.println(houses);
  }

  @SuppressWarnings("all")
  public List<House> filterHouses(Map<String, Object> filters) throws IOException {
    List<House> houses = getHouses();
    System.out.println(houses);
    System.out.println("----------------------------");
    System.out.println(filters);

    if (filters.size() < 1)
      return getHouses();

    Object kw = filters.get("keyword");
    String keyword = kw == null ? null : kw.toString();

    Object pt = filters.get("propertyType");
    String propertyType = pt == null ? null : pt.toString();

    Object mnp = filters.get("minPrice");
    Integer minPrice = mnp == null ? null : Integer.valueOf(mnp.toString());

    Object mxp = filters.get("maxPrice");
    Integer maxPrice = mxp == null ? null : Integer.valueOf(mxp.toString());

    Object s = filters.get("slots");
    Integer slots = s == null ? null : Integer.valueOf(s.toString());

    Object r = filters.get("rating");
    Double rating = r == null ? null : Double.valueOf(Integer.valueOf(r.toString()));

//    List<String> amenities = (List) filters.get("amenities");

    if (keyword != null) {
      houses = houses.stream()
          .filter(house ->
                      house.getTitle().equals(keyword) ||
                          house.getCity().equals(keyword) ||
                          house.getCountry().equals(keyword) ||
                          house.getState().equals(keyword) ||
                          house.getStreet().equals(keyword)

                 )
          .collect(Collectors.toList());
    }

    System.out.println("keyword");
    System.out.println(houses);

    if (propertyType != null)
      houses = houses.stream()
          .filter(house -> {
                    return house.getPropertyType().equals(propertyType);

                  }
                 )
          .collect(Collectors.toList());

    System.out.println("propertyType");
    System.out.println(houses);


    if (slots != null)
      houses = houses.stream()
          .filter(house -> {
                    return house.getSlots() >= slots;

                  }
                 )
          .collect(Collectors.toList());

    System.out.println("slots");
    System.out.println(houses);

    if (rating != null)
      houses = houses.stream()
          .filter(house -> {
                    Double average = house.getAverage();
                    return average == null ? false : average >= rating;
                  }
                 )
          .collect(Collectors.toList());
    houses = houses.stream()
        .filter(house -> {
          Integer minP = minPrice;
          if (minP == null)
            minP = 0;
          Integer maxP = maxPrice;
          if (maxP == null)
            maxP = Integer.MAX_VALUE;

          return house.getPrice() >= minP && house.getPrice() <= maxP;
        })
        .collect(Collectors.toList());

    return houses;
  }


  public List<House> getHouses() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    List<House> houses = mapper.readValue(bale, new TypeReference<List<House>>() {});
    return houses;
  }

  private String bale = "[\n" +
      "\t{\n" +
      "\t    \"coverPic\": \"http://localhost:8080/api/images/M192YTVDc09DLmpwZWdDaGlrYUZ1aml3YXJh.jpeg\",\n" +
      "\t    \"title\": \"dreamy\",\n" +
      "\t    \"propertyType\": \"Rooms\",\n" +
      "\t    \"amenities\": [],\n" +
      "\t    \"street\": \"street\",\n" +
      "\t    \"city\": \"city\",\n" +
      "\t    \"state\": \"state\",\n" +
      "\t    \"country\": \"country\",\n" +
      "\t    \"price\": 4500,\n" +
      "\t    \"description\": \"desc\",\n" +
      "\t    \"slots\": 4,\n" +
      "\t    \"average\": 3,\n" +
      "\t    \"houseReviews\": []\n" +
      "\t},\n" +
      "\t{\n" +
      "\t    \"coverPic\": \"http://localhost:8080/api/images/bmFuZGVtb25haS5QTkdDaGlrYUZ1aml3YXJh.PNG\",\n" +
      "\t    \"title\": \"moonbae\",\n" +
      "\t    \"propertyType\": \"Rooms\",\n" +
      "\t    \"amenities\": [],\n" +
      "\t    \"street\": \"country\",\n" +
      "\t    \"city\": \"siti\",\n" +
      "\t    \"state\": \"stayt\",\n" +
      "\t    \"country\": \"street\",\n" +
      "\t    \"price\": 3000,\n" +
      "\t    \"description\": \"desc\",\n" +
      "\t    \"slots\": 5,\n" +
      "\t    \"average\": 4,\n" +
      "\t    \"houseReviews\": []\n" +
      "\t},\n" +
      "\t{\n" +
      "\t    \"coverPic\": \"http://localhost:8080/api/images/Y2hvaS5QTkdDaGlrYUZ1aml3YXJh.PNG\",\n" +
      "\t    \"title\": \"choi dal dal\",\n" +
      "\t    \"propertyType\": \"Apartment\",\n" +
      "\t    \"amenities\": [],\n" +
      "\t    \"street\": \"stret\",\n" +
      "\t    \"city\": \"kete\",\n" +
      "\t    \"state\": \"stet\",\n" +
      "\t    \"country\": \"kantry\",\n" +
      "\t    \"price\": 500,\n" +
      "\t    \"description\": \"dsec\",\n" +
      "\t    \"slots\": 6,\n" +
      "\t    \"average\": 5,\n" +
      "\t    \"houseReviews\": []\n" +
      "\t}\n" +
      "]";
}
