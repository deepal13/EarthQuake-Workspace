package akky.pk1;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;

public class Quake {
	private Date date;
	private String details;
	private Location location;
	private double magnitude;
	private String link;
	
	public Date getDate() { return date; }
	public String getDetails() { return details; }
	public Location getLocation() { return location; }
	public double getMagnitude() { return magnitude; }
	public String getLink() { return link; }
	
	public Quake(Date d, String det, Location loc, double mag,
			String lnk) {
			date = d;
			details = det;
			location = loc;
			magnitude = mag;
			link = lnk;
			}
	@Override
	public String toString() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
		String dateString = sdf.format(date);
		return dateString + ": " + magnitude + " " + details;
		// TODO Auto-generated method stub
		//return super.toString();
	}
	

}
