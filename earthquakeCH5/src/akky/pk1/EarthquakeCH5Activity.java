package akky.pk1;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element; 
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;





import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;





import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import android.widget.Toast;



public class EarthquakeCH5Activity extends Activity {
	EarthquakeReceiver receiver;
	int minimumMagnitude = 0;
	boolean autoUpdate = false;
	int updateFreq = 0;
	private static final int SHOW_PREFERENCES = 1;
	static final private int QUAKE_DIALOG = 1;
	Quake selectedQuake;
	static final private int MENU_UPDATE = Menu.FIRST;
	static final private int MENU_PREFERENCES = Menu.FIRST+1;
	ListView earthquakeListView;
	ArrayAdapter<Quake> aa;
	ArrayList<Quake> earthquakes = new ArrayList<Quake>();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toast.makeText(this, "Oncreate", Toast.LENGTH_LONG).show();
        earthquakeListView = (ListView)this.findViewById(R.id.listView1);
        
        earthquakeListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView av, View v, int index,
					long arg3) {
				// TODO Auto-generated method stub
				selectedQuake = earthquakes.get(index);
				showDialog(QUAKE_DIALOG);
				
			}
       // 	public void onItemClick(AdapterView _av, View _v, int _index,
       // 	long arg3) {
        //	selectedQuake = earthquakes.get(_index);
        //	showDialog(QUAKE_DIALOG);
        //	}
        	});
        		int layoutID = android.R.layout.simple_list_item_1;
        		aa = new ArrayAdapter<Quake>(this, layoutID , earthquakes);
        		earthquakeListView.setAdapter(aa);
        		 loadQuakesFromProvider();
        		updateFromPreferences();
        		
        		refreshEarthquakes();
    }
    
    private void refreshEarthquakes() {
    	startService(new Intent(this, EarthquakeService.class));
    	}
    
  /*  public void refreshEarthquakes()
    {
    	URL url;
    	try {
    	String quakeFeed = getString(R.string.quake_feed);
    	url = new URL(quakeFeed);
    	URLConnection connection;
    	connection = url.openConnection();
    	HttpURLConnection httpConnection = (HttpURLConnection)connection;
    	int responseCode = httpConnection.getResponseCode();
    	
    	Toast.makeText(this, "get response code", Toast.LENGTH_LONG).show();
    	
    	if (responseCode == HttpURLConnection.HTTP_OK) {
    		InputStream in = httpConnection.getInputStream();
    	
    		Toast.makeText(this, "i m in if", Toast.LENGTH_LONG).show();
    		
    		DocumentBuilderFactory dbf;
    		dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db = dbf.newDocumentBuilder();
    	//Defines a factory API that enables applications to obtain a 
    		//parser that produces DOM object trees from XML documents.
    		Toast.makeText(this, "after doc builder", Toast.LENGTH_LONG).show();
    		// Parse the earthquake feed.
    		Document dom = db.parse(in);
    		Element docEle = dom.getDocumentElement();
    		// Clear the old earthquakes
    		earthquakes.clear();
    		
    		loadQuakesFromProvider();
    		Toast.makeText(this, "Clear the old earthquakes", Toast.LENGTH_LONG).show();
    		
    		NodeList nl = docEle.getElementsByTagName("entry");
    		if (nl != null && nl.getLength() > 0) {
    			Toast.makeText(this, "i m in if 2", Toast.LENGTH_LONG).show();
    			for (int i = 0 ; i < nl.getLength(); i++) {
    				Element entry = (Element)nl.item(i);
    				Element title =
    				(Element)entry.getElementsByTagName("title").item(0);
    				Element g =
    				(Element)entry.getElementsByTagName("georss:point").item(0);
    				Element when =
    				(Element)entry.getElementsByTagName("updated").item(0);
    				Element link =
    				(Element)entry.getElementsByTagName("link").item(0);
    				String details = title.getFirstChild().getNodeValue();
    				String hostname = "http://earthquake.usgs.gov";
    				String linkString = hostname + link.getAttribute("href");
    				String point = g.getFirstChild().getNodeValue();
    				String dt = when.getFirstChild().getNodeValue();
    				SimpleDateFormat sdf;
    				sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
    				Date qdate = new GregorianCalendar(0,0,0).getTime();
    			//	Toast.makeText(this, "inside for", Toast.LENGTH_SHORT).show();
    				try {
    					qdate = sdf.parse(dt);
    					}
    				catch (ParseException e)
    				    {
    					e.printStackTrace(); 
    					}
    				
    				String[] location = point.split(" ");
    				Location l = new Location("dummyGPS");
    				l.setLatitude(Double.parseDouble(location[0]));
    				l.setLongitude(Double.parseDouble(location[1]));
    				String magnitudeString = details.split(" ")[1];
    				
    			//	Toast.makeText(this, "abhi tak thik hai re", Toast.LENGTH_SHORT).show();
    				int end = magnitudeString.length()-1;
    				double magnitude;
    				magnitude = Double.parseDouble(magnitudeString.substring(0,end));
    				details = details.split(",")[1].trim();
    				Quake quake = new Quake(qdate, details, l,
    						magnitude, linkString);
    				
    				addNewQuake(quake);
    			}
    		}
    	}
    	 
    	
    	
    	}
    	catch (MalformedURLException e) {
    		e.printStackTrace();
    		} catch (IOException e) {
    		e.printStackTrace();
    		} catch (ParserConfigurationException e) {
    		e.printStackTrace();
    		}catch (SAXException e) {
    		e.printStackTrace();
    		}
    		finally {
    		}
    	
    }
*/
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		
		
		switch(id) {
		case (QUAKE_DIALOG) :
		LayoutInflater li = LayoutInflater.from(this);
		View quakeDetailsView = li.inflate(R.layout.quake_details, null);
		AlertDialog.Builder quakeDialog = new AlertDialog.Builder(this);
		quakeDialog.setTitle("Quake Time");
		quakeDialog.setView(quakeDetailsView);
		return quakeDialog.create();
		}
		return null;
		//return super.onCreateDialog(id);
		
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onPrepareDialog", Toast.LENGTH_SHORT).show();                                                                                
		switch(id) {
		case (QUAKE_DIALOG) :
			
			
		SimpleDateFormat sdf;
	sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateString = sdf.format(selectedQuake.getDate());
		String quakeText = "Mangitude " + selectedQuake.getMagnitude() + "\n" + selectedQuake.getDetails() + "\n" + selectedQuake.getLink();
		AlertDialog quakeDialog = (AlertDialog)dialog;
		quakeDialog.setTitle(dateString);
		Toast.makeText(this, "onPrepareDialog case", Toast.LENGTH_SHORT).show();
		TextView tv =(TextView)quakeDialog.findViewById(R.id.quakeDetailsTextView);
	    tv.setText(quakeText);
		break;
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
		menu.add(0, MENU_UPDATE, Menu.NONE, R.string.menu_update);
		menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
			super.onOptionsItemSelected(item);
			
			
			switch (item.getItemId()) {
			case (MENU_UPDATE): {
			refreshEarthquakes();
			return true;
			}
			case (MENU_PREFERENCES): {
				Intent i = new Intent(this, Preferences.class);
				startActivityForResult(i, SHOW_PREFERENCES);
				return true;
				}
			}
			return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		
		if (requestCode == SHOW_PREFERENCES)
			if (resultCode == Activity.RESULT_OK) {
			updateFromPreferences();
			//refreshEarthquakes();
			}
	}

	/*public  void addNewQuake(Quake _quake) {
  ContentResolver cr = getContentResolver();
		  
		  // Construct a where clause to make sure we don�t already have this 
		  // earthquake in the provider.
		  String w = EarthquakeProvider.KEY_DATE + " = " + _quake.getDate().getTime();

		  // If the earthquake is new, insert it into the provider.
		  if (cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null).getCount() == 0){
		    ContentValues values = new ContentValues();    

		    values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
		    values.put(EarthquakeProvider.KEY_DETAILS, _quake.getDetails());

		    double lat = _quake.getLocation().getLatitude();
		    double lng = _quake.getLocation().getLongitude();
		    values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
		    values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
		    values.put(EarthquakeProvider.KEY_LINK, _quake.getLink());
		    values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());

		    cr.insert(EarthquakeProvider.CONTENT_URI, values);
		    earthquakes.add(_quake);

		    addQuakeToArray(_quake);
		}
	}
	*/
	
	private void updateFromPreferences() {
		SharedPreferences prefs =
		getSharedPreferences(Preferences.USER_PREFERENCE,
		Activity.MODE_PRIVATE);
		int minMagIndex = prefs.getInt(Preferences.PREF_MIN_MAG, 0);
		if (minMagIndex < 0)
		minMagIndex = 0;
		int freqIndex = prefs.getInt(Preferences.PREF_UPDATE_FREQ, 0);
		if (freqIndex < 0)
		freqIndex = 0;
		autoUpdate = prefs.getBoolean(Preferences.PREF_AUTO_UPDATE, false);
		Resources r = getResources();
		// Get the option values from the arrays.
		int[] minMagValues = r.getIntArray(R.array.magnitude);
		int[] freqValues = r.getIntArray(R.array.update_freq_values);
		// Convert the values to ints.
		minimumMagnitude = minMagValues[minMagIndex];
		updateFreq = freqValues[freqIndex];
	}
	
	
	 private void loadQuakesFromProvider() {
		  	// Clear the existing earthquake array
		  	earthquakes.clear();
		  
		  	ContentResolver cr = getContentResolver();

			  // Return all the saved earthquakes
			  Cursor c = cr.query(EarthquakeProvider.CONTENT_URI, null, null, null, null);
			 
			  if (c.moveToFirst()) {
		      do { 
		        // Extract the quake details.
		        Long datems = c.getLong(EarthquakeProvider.DATE_COLUMN);
		        String details = c.getString(EarthquakeProvider.DETAILS_COLUMN);
		        Float lat = c.getFloat(EarthquakeProvider.LATITUDE_COLUMN);
		        Float lng = c.getFloat(EarthquakeProvider.LONGITUDE_COLUMN);
		        Double mag = c.getDouble(EarthquakeProvider.MAGNITUDE_COLUMN);
		        String link = c.getString(EarthquakeProvider.LINK_COLUMN);

		        Location location = new Location("gps");
		        location.setLongitude(lng);
		        location.setLatitude(lat);

		        Date date = new Date(datems);

		        Quake q = new Quake(date, details, location, mag, link);
		        addQuakeToArray(q);
		      } while(c.moveToNext());
			  }
			  c.close();
			}
	 
	 private void addQuakeToArray(Quake _quake) {
		  if (_quake.getMagnitude() > minimumMagnitude) {
		    // Add the new quake to our list of earthquakes.
		    earthquakes.add(_quake);

		    // Notify the array adapter of a change.
		    aa.notifyDataSetChanged();
		  }
		}

	
	 @Override
	protected void onPause() {
		 Toast.makeText(this, "pause", Toast.LENGTH_LONG).show();
		// TODO Auto-generated method stub
		 unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "resume", Toast.LENGTH_LONG).show();
		IntentFilter filter;
		filter = new IntentFilter(EarthquakeService.NEW_EARTHQUAKE_FOUND);
		receiver = new EarthquakeReceiver();
		registerReceiver(receiver, filter);
		loadQuakesFromProvider();
		super.onResume();
	}


	public class EarthquakeReceiver extends BroadcastReceiver {
		 @Override
		 public void onReceive(Context context, Intent intent) {
		 loadQuakesFromProvider();
		 }

		
		 }
	
}
   