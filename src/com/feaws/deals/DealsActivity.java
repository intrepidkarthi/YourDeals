package com.feaws.deals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.feaws.utils.Constants;
import com.feaws.utils.ImageLoader;
import com.feaws.utils.JSONParser;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DealsActivity extends Activity {
	ImageLoader imageLoader;
	ProgressBar progressBar;
	ListView listview;
	ArrayList<HashMap<String, String>> deals_list = new ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deals);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.VISIBLE);
		listview = (ListView) findViewById(R.id.list);
		new GetTimesDeal().execute(Constants.deals_url);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Intent intent = new Intent(DealsActivity.this,
						BrowserActivity.class);
				intent.putExtra("url", deals_list.get(arg2).get("DealUrl"));
				startActivity(intent);

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deals, menu);
		return true;
	}

	private class GetTimesDeal extends
			AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
		JSONParser jParser;
		JSONObject jObject;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {

			jParser = new JSONParser();
			jObject = jParser.getJSONFromUrl(params[0]);
			JSONArray jArray = new JSONArray();
			try {
				jArray = jObject.getJSONArray("DealDetails");

				for (int i = 0; i < jArray.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject obj = jArray.getJSONObject(i);
					map.put("DealId", obj.getString("DealId"));
					map.put("DealDescription", obj.getString("DealDescription"));
					map.put("DealShortDescription",
							obj.getString("DealShortDescription"));
					map.put("DealLocation", obj.getString("DealLocation"));
					map.put("DealOriginalvalue",
							obj.getString("DealOriginalvalue"));
					map.put("DealPayInAdvance",
							obj.getString("DealPayInAdvance"));
					map.put("DealPayToMerchant",
							obj.getString("DealPayToMerchant"));
					map.put("DealDiscountGiven",
							obj.getString("DealDiscountGiven"));
					map.put("AmountSavedByUser",
							obj.getString("AmountSavedByUser"));
					map.put("DealUrl", obj.getString("DealUrl"));
					map.put("DealEndDate", obj.getString("DealEndDate"));
					JSONArray arr = new JSONArray();
					JSONObject jo = new JSONObject();
					jo = obj.getJSONObject("DealImages");
					arr = jo.getJSONArray("image_path");
					map.put("DealImage", arr.getString(0));
					map.put("MerchantName", obj.getString("MerchantName"));

					deals_list.add(i, map);

				}

				return deals_list;

			} catch (JSONException e) {
				return null;

			}

		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {

			progressBar.setVisibility(View.GONE);

			if (result != null) {
				listview.setAdapter(new ListViewAdapter(
						getApplicationContext(), result));

			} else {
				Toast.makeText(getApplicationContext(), "Try later!", 3000)
						.show();
			}

		}

	}

	private class ListViewAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> input = new ArrayList<HashMap<String, String>>();
		Context a;
		private LayoutInflater inflater = null;

		ListViewAdapter(Context context,
				ArrayList<HashMap<String, String>> dealsinput) {
			a = context;
			input = dealsinput;
			inflater = (LayoutInflater) a
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageLoader = new ImageLoader(a.getApplicationContext());
		}

		private int calcDays(String input) throws ParseException {

			Date todaysdate = new Date();
			SimpleDateFormat of = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat nf = new SimpleDateFormat("dd/MM/yyyy");

			String today = new SimpleDateFormat("dd/MM/yyyy")
					.format(todaysdate);
			String deal_end_date = nf.format(of.parse(input));
			// Log.v("today", today + "       input:" + deal_end_date +
			// "   actual"
			// + input);
			int days = calculateDays(today, deal_end_date);
			return days;
		}

		private int calculateDays(String startDate, String endDate)
				throws ParseException {
			Date sDate = new SimpleDateFormat("dd/MM/yyyy").parse(startDate);
			Date eDate = new SimpleDateFormat("dd/MM/yyyy").parse(endDate);
			Calendar cal3 = Calendar.getInstance();
			cal3.setTime(sDate);
			Calendar cal4 = Calendar.getInstance();
			cal4.setTime(eDate);
			return daysBetween(cal3.getTime(), cal4.getTime());
		}

		/** Using Calendar - THE CORRECT WAY **/
		private int daysBetween(Date startDate, Date endDate) {
			return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
		}

		@Override
		public int getCount() {
			return input.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (convertView == null)
				view = inflater.inflate(R.layout.dealitem_layout, null);
			System.out.println("deal ling   "
					+ input.get(position).get("DealImage"));
			ImageView iv = (ImageView) view.findViewById(R.id.deal_image);
			TextView deal_short_desc = (TextView) view
					.findViewById(R.id.deal_short_description);
			TextView deal_desc = (TextView) view
					.findViewById(R.id.deal_description);
			TextView discount = (TextView) view.findViewById(R.id.savings);
			TextView new_price = (TextView) view.findViewById(R.id.newprice);
			TextView old_price = (TextView) view.findViewById(R.id.oldprice);
			TextView days = (TextView) view.findViewById(R.id.days);

			days.setText("End Date: " + input.get(position).get("DealEndDate"));

			new_price.setText("Rs."
					+ input.get(position).get("DealPayToMerchant"));
			old_price.setText("Rs."
					+ input.get(position).get("DealOriginalvalue"));
			old_price.setPaintFlags(old_price.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			discount.setText("Discount: "
					+ input.get(position).get("DealDiscountGiven"));
			deal_desc.setText(input.get(position).get("DealDescription"));
			deal_short_desc.setText(input.get(position).get(
					"DealShortDescription"));
			if (input.get(position).get("DealImage") != null)
				imageLoader.DisplayImage(input.get(position).get("DealImage"),
						iv);

			return view;
		}

	}

}
