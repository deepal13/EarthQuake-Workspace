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
	static final private int QUAKE_DIALOG = 1;
	Quake selectedQuake;
	static final private int MENU_UPDATE = Menu.FIRST;
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
        		refreshEarthquakes();
    }
    
    public void refreshEarthquakes()
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
			}
			return false;
	}

	public  void addNewQuake(Quake quake) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "addnewquake", Toast.LENGTH_LONG).show();
		earthquakes.add(quake);
		aa.notifyDataSetChanged();
	}
    
}
   