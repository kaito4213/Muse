package model.reservation;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import dao.ServerInterface;
import model.flight.Flight;
import model.flight.Flights;

public class Reservation implements Comparable<Reservation>{
	private int index;
	private Flights legs;
	private float totalPrice;
	private float travelTime;
	private String mSeatPreference;
	private final String mTeamName = "Muse";
	
	
	/**
	 * Initializing constructor.
	 * 
	 * All attributes are initialized with input values
	 * @param flights a list of flights 
	 * @param SeatType a String "Coach" or "FirstClass" indicate 
	 * 		  which kind of seat is preferred 
	 * @param index unique index for each reservation
	 */
	public Reservation(Flights flights, String SeatType, int index) {
		this.index = index;
		legs = flights;
		totalPrice = getTotalPrice();
		travelTime = getTotalTime();
		mSeatPreference = SeatType;
	}
	
	public int getIndex() {
		return index;
	}

	/**
	 * This method calculate the total price of flights in a reservation
	 * @return totalPrice
	 */ 
	public float getTotalPrice() {
		totalPrice = 0.00f;
		
		for (int i = 0; i < legs.size(); i++) {			
			if (mSeatPreference == "Coach") {
				totalPrice += Float.parseFloat(legs.get(i).getCoachPrice().replaceAll("[^\\d.]+", ""));
			}
			else {
				totalPrice +=  Float.parseFloat(legs.get(i).getFirstClassPrice().replaceAll("[^\\d.]+", ""));
			}	 
		}
		
		return totalPrice;
	}
	
	/**
	 * get total travel time of the reservation option
	 * @return the total travel time from start to finish
	 */
	public float getTotalTime() {
		int numLegs = legs.size();
		travelTime = 0.0f;
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy MMM d HH:mm z", Locale.US);
		if (legs == null || numLegs == 0) {
			return 0.00f;
		}
		
		// get start time of first leg
		// get arrival time of last leg
		// subtract arrival from start
		LocalDateTime departTimeLocal = LocalDateTime.parse(legs.get(0).getDepartureAirportTime(), dateFormat);
		LocalDateTime arrivalTimeLocal = LocalDateTime.parse(legs.get(numLegs - 1).getArrivalAirportTime(), dateFormat);	
		float diffInMinutes = java.time.Duration.between(departTimeLocal, arrivalTimeLocal).toMinutes();
		travelTime += diffInMinutes;		
		
		return travelTime;
	}
	
	/**
	 * This method return departure time
	 * 
	 * @return departure time in LocalDateTime format
	 */
	public LocalDateTime getDepartureAirportTime() {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy MMM d HH:mm z", Locale.US);
		if (legs == null || legs.size() == 0) {
			return null;
		}
		
		LocalDateTime departTimeLocal = LocalDateTime.parse(legs.get(0).getDepartureAirportTime(), dateFormat);
		return departTimeLocal;
	}
	
	/**
	 * This method return arrival time
	 * 
	 * @return Arrival time in LocalDateTime format
	 */
	public LocalDateTime getArrivalAirportTime() {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy MMM d HH:mm z", Locale.US);
		if (legs == null || legs.size() == 0) {
			return null;
		}
		
		LocalDateTime arrivalTimeLocal = LocalDateTime.parse(legs.get(legs.size() - 1).getArrivalAirportTime(), dateFormat);
		return arrivalTimeLocal;
	}
	
	/**
	 * This method format time to hh:mm format
	 * 
	 * @return total flight time in hh:mm format
	 */
	public String totalTimeString() {
		int hours = (int) (travelTime / 60);
		int minutes = (int) (travelTime % 60);
		return String.format("%02d:%02d", hours, minutes);
	}

	
	/**
	 * Show the information for this reservation
	 */
	public String toString() {
		int numLegs = legs.size();
		String leg1DepAirport , leg2DepAirport = " ", leg3DepAirport = " ", arrivalAirport;
		int deptcode1 = 0, deptcode2 = 0, deptcode3 = 0, arrivalcode = 0;
		
		leg1DepAirport = legs.get(0).getDepartureAirport();
		arrivalAirport = legs.get(0).getArrivalAirport();
		
		deptcode1 = legs.get(0).getNumber();
		if (numLegs > 1) {
			leg2DepAirport = legs.get(1).getDepartureAirport();
			arrivalAirport = legs.get(1).getArrivalAirport();
			deptcode2 = legs.get(1).getNumber();
		}
		if(numLegs > 2) {
			leg3DepAirport = legs.get(2).getDepartureAirport();
			arrivalAirport = legs.get(2).getArrivalAirport();
			deptcode3 = legs.get(2).getNumber();
		}
		arrivalcode = legs.get(legs.size() - 1).getNumber();
		
		String departureAirportTime1 = " ";
		String departureAirportTime2 = " ";
		String departureAirportTime3 = " ";
		try {
			departureAirportTime1 = legs.get(0).getLocalDepTime();
			if (numLegs > 1) {
				departureAirportTime2 = legs.get(1).getLocalDepTime();
			}
			if (numLegs > 2) {
				departureAirportTime3 = legs.get(2).getLocalDepTime();
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String arrivalAirportTime = "";
		try {
			arrivalAirportTime = legs.get(legs.size() - 1).getLocalArrTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// print first leg
		StringBuffer sb = new StringBuffer();	
		sb.append("Index: ").append(index).append("\n");
		sb.append("Flight number: ").append(deptcode1).append(", ");
		sb.append("Departure: ").append(leg1DepAirport).append(", ");
		sb.append(departureAirportTime1).append("\n");
		
		// print second leg if any
		if (numLegs > 1) {
			sb.append("Flight number: ").append(deptcode2).append(", ");
			sb.append("Departure: ").append(leg2DepAirport).append(", ");
			sb.append(departureAirportTime2).append("\n");
		}
		
		// print third leg if any
		if (numLegs > 2) {
			sb.append("Flight number: ").append(deptcode3).append(", ");
			sb.append("Departure: ").append(leg3DepAirport).append(", ");
			sb.append(departureAirportTime3).append("\n");
		}
		
		DecimalFormat df = new DecimalFormat("#.00");
	    String totalPriceFormatted = df.format(totalPrice);
		
		sb.append("Arrival: ").append(arrivalAirport).append(", ");
		sb.append(arrivalAirportTime).append("\n");
		sb.append("Duration: ").append(totalTimeString()).append(", ");
		sb.append("Price: $").append(totalPriceFormatted).append(", ");
		sb.append("stop over: ").append(legs.size()-1).append("\n");
		return sb.toString();
	}
	
	/**Show the detailed information as for a confirmation for the user
	 * 
	 * @return string concludes detail flight infomation
	 */
	public String details() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i< legs.size(); i++) {
			sb.append(legs.get(i).toString());
		}
		return sb.toString();
	}
	
	/**
	 * Default comparator
	 * Compare two Reservation based on number of flights
	 * 
	 * @return results of String.compareToIgnoreCase
	 */
	public int compareTo(Reservation other) {
		return this.legs.size() - other.legs.size() ;		
	}
	
	/**
	 * Convert a reservation to XML string 
	 * @return the XML string used in flight reservation
	 */
	public String toXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<Flights>");
		for (int i=0; i < legs.size(); i++) {
			sb.append("<Flight number=\"");
			sb.append(String.valueOf(legs.get(i).getNumber()));
			sb.append("\" seating=\"" + mSeatPreference + "\"/>");
		}
		sb.append("</Flights>");
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	/**
	 * This method will make reservation for all flight in legs
	 */
	public void confirmReservation() {
		ServerInterface.INSTANCE.unlock(mTeamName);
		ServerInterface.INSTANCE.lock(mTeamName);
		ServerInterface.INSTANCE.postFlights(mTeamName, this);
	}
	
}

