package com.ecfeed.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ecfeed.junit.annotation.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.firefox.FirefoxDriver;

//@EcFeedService("https://localhost:8090")
//@EcFeedKeyStore("src/test/resources/security")
//@EcFeedModel("TestUuid1")

@EcFeedService("https://prod-gen.ecfeed.com")
@EcFeedModel("5905-6568-3203-5902-2571")

//@EcFeedService("https://development-gen.ecfeed.com")
//@EcFeedModel("6572-8506-3585-3035-5924")

@Disabled
public class FlightFinderTest {

//	@EcFeedTest
//	@EcFeedInput("'dataSource':'genNWise','coverage':'100','n':'2', 'constraints':'ALL'")
//	public void findFlightsTest(String airportFrom, String airportTo, int daysToFlyOut, boolean isReturnFlight, int daysBetweenFlights, TicketClass ticketClass, float maxPrice) {
//		System.out.println(airportFrom + " " + airportTo + " " + daysToFlyOut + " " + isReturnFlight + " " + daysBetweenFlights + " " + ticketClass + " " + maxPrice);
//	}
	
	private WebDriver fDriver;
	
	@BeforeAll
	public static void setDriverProperty() {
		System.setProperty("webdriver.gecko.driver", "/home/krzysztof/geckodriver");
	}

	@BeforeEach
	public void setDriver() {
		fDriver = new FirefoxDriver();
		fDriver.get("http://ecfeed.com/samples/FlightFinder");
	}

	@AfterEach
	public void removeDriver() {
		fDriver.quit();
	}

	@Test
	public void initialTest() {

		try {
			sleep(3000);
		} finally {
			fDriver.quit();
		}

	}
	
	@EcFeedTest
	@EcFeedInput("'dataSource':'genNWise','coverage':'100','n':'2', 'constraints':'ALL'")
    public void findFlightsTest(String airportFrom, String airportTo, int daysToFlyOut, boolean isReturnFlight, int daysBetweenFlights, TicketClass ticketClass, float maxPrice) {

    	if (airportFrom.equals(airportTo)) {
    		return;
    	}

    	setFields(airportFrom, airportTo, daysToFlyOut, isReturnFlight, daysBetweenFlights, ticketClass);
    	fDriver.findElement(By.id("searchFlights")).click();
    	sleep(1000);
    	assertTrue(getMaxPrice() <= maxPrice);

    }
    
    private void setFields(String airportFrom, String airportTo, int daysToFlyOut, boolean isReturnFlight, int daysBetweenFlights, TicketClass ticketClass) {

    	setAirports(airportFrom, airportTo);
    	Date today = new Date();
    	Date flyOutDate = addDays(today, daysToFlyOut);
    	Date returnDate = addDays(today, daysToFlyOut + daysBetweenFlights);    
    	setDate("dateFlyOut", flyOutDate);

    	if (isReturnFlight){
    		setDate("dateReturn", returnDate);
    	}

    	setJourneyType(isReturnFlight);
    	setTicketClass(ticketClass);
    	
    }
    
    private void setAirports(String airportFrom, String airportTo) {
        setSelectField("airportFrom", airportFrom);
        setSelectField("airportTo", airportTo);
    }
    
    private void setDate(String prefix, Date date) {
        setDateFields(prefix + "Year", prefix + "Month", prefix + "Day", date);
    }
    
    private void setDateFields(String yearId, String monthId, String dayId, Date dateToSet) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateToSet);

        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.format("%02d", calendar.get(Calendar.MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));

        setSelectField(yearId, year);
        setSelectField(monthId, month);
        setSelectField(dayId, day);
    }
    
    private void setJourneyType(boolean journeyType) {
        String id;

        if (journeyType == true) {
            id = "radioReturn";
        } else {
            id = "radioOneWay";
        }

        fDriver.findElement(By.id(id)).click();
    }
    
    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }
    
    private void setTicketClass(TicketClass ticketClass) {
        setSelectField("ticketClass", ticketClassName(ticketClass));
    }
    
    private String ticketClassName(TicketClass ticketClass) {

	    switch (ticketClass) {
            case ECONOMY: return "Economy";
            case PREMIUM_ECONOMY: return "Premium economy";
            case BUSINESS: return "Business";
            case FIRST: return "First";
            default: throw new RuntimeException("Invalid ticket class.");
        }

    }
    
    private void setSelectField(String fieldId, String value) {
        WebElement select = fDriver.findElement(By.id(fieldId));
        List<WebElement> options = select.findElements(By.tagName("option"));

        for(WebElement option : options){
            if(option.getText().equals(value)) {
                option.click();
                break;
            }
        }

    }
    
    private float getMaxPrice() {
        WebElement maxPriceElement = fDriver.findElement(By.id("maxPrice"));

        if (maxPriceElement == null) {
            return 0;
        }

        return Float.parseFloat(maxPriceElement.getText());
    }
    
    private void sleep(int milliseconds) {

	    try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

    }      
    
}
