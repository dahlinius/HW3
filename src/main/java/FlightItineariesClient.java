/**
 * Created by Rickard on 2016-02-23.
 */
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.util.JAXBResult;
import java.util.ArrayList;
import java.util.List;


public class FlightItineariesClient {



    public static void main(String[] args) {

        String baseURL = "http://localhost:8080/HW3/rest/";

        Client cln = ClientBuilder.newClient();
        try {

            //AUTHORIZATION
            String token = cln
                    .target(baseURL + "flightItinearies/authorize/Anton&Dahlin")
                    .request(MediaType.TEXT_PLAIN).get(String.class);
            System.out.println(token);

            System.out.println("Authorzation Response: "+token);

            //FLIGHT ITINEARIES FOR GIVEN DEPARTURE AND DESTINATION
            Response flightItineariesResponse = cln
                    .target(baseURL + "flightItinearies/getFlightItinearies/Stockholm&Moskva/"+token)
                    .request(MediaType.APPLICATION_XML)
                    .get();
            ArrayList<FlightItinerary> response = flightItineariesResponse.readEntity(new GenericType<ArrayList<FlightItinerary>>(){});
            
            for(FlightItinerary fi: response){
                System.out.println("FlightItineraryID:"+"["+fi.getId()+"]\n");
                for(Flight fl: fi.getFlights()){
                    System.out.println(fl.toString());
                }
                System.out.println("\n\n\n");
            }


            //FLIGHT PRICES FOR A GIVEN DATE
            Response flightPricesResponse = cln
                    .target(baseURL + "flightItinearies/getFlightPrices/2016-02-01/"+token)
                    .request(MediaType.APPLICATION_XML)
                    .get();

            ArrayList<Flight> response1 = flightPricesResponse.readEntity(new GenericType<ArrayList<Flight>>(){});

            for(Flight fl: response1){
                System.out.println(fl.toString());
            }


            String username ="Ulf";
            String password = "Brandeby";

            GenericType<JAXBElement<User>> user = new GenericType<JAXBElement<User>>(){};


            //NEW USER
            try {
                User newUser = new User("Brun", "Brun");
                Response newUserResponse = cln
                        .target(baseURL + "users/create")
                        .request(MediaType.TEXT_PLAIN)
                        .put(Entity.entity(newUser, MediaType.APPLICATION_XML));

                if(newUserResponse.getStatusInfo().getReasonPhrase().equals("Bad Request")){
                    System.out.println("User already exists");
                }else{
                    System.out.println("User successfully created");
                }

                System.out.println(newUserResponse.getStatusInfo().getReasonPhrase());
            }catch(BadRequestException ex){
                System.out.println(ex.getMessage());
            }

            try {


                String creditCardNumber = "1337";

                String issueTicketsResponse = cln
                        .target(baseURL+"flightItinearies/issueTickets/1337/"+token)
                        .request(MediaType.TEXT_PLAIN)
                        .post(Entity.entity(creditCardNumber, MediaType.TEXT_PLAIN), String.class);

                System.out.println("Response from ticket: "+issueTicketsResponse.toString());

            }catch(Exception ex){
                ex.printStackTrace();
            }




        }catch(NotAuthorizedException ex){
            System.out.println("Not authorized");
        }

//        try {
//            String resp = cln
//                    .target(baseURL + "flightItinearies/bookItinerary/2"+token)
//                    .request(MediaType.TEXT_PLAIN).get(String.class);
//            System.out.println(resp);
//        }catch(NotAuthorizedException ex){
//            System.out.println("Not authorized");
//        }
    }
}
