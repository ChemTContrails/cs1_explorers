package cs1.softwareProject.explorers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cs1.softwareProject.explorers.createGroup.CreateGroup;
import android.app.Activity;
//import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupProfile extends Activity {

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private String jsonResult;
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String url = "http://doc.gold.ac.uk/~ma301ma/IgorFile/mrX.php";
	private static final String LOGIN_URL_userJoined = "http://doc.gold.ac.uk/~ma301ma/IgorFile/submitUserJoined.php";

	public static List<userObject> joined_user = new userData().getUsers();

	ArrayList<Integer> map = new ArrayList<Integer>();
	ArrayList<Integer> value = new ArrayList<Integer>();

	List<userObject> oragnisedUsers;
	ListView userList;
	int user_id;
	int group_id;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.groupprofile);
		
		
		accessWebService();
		Intent intent = getIntent();
		
		group_id = intent.getIntExtra("groupId", 0);
		String location = intent.getStringExtra("location");
		String name = intent.getStringExtra("EventName");
		String time = intent.getStringExtra("time");
		String description = intent.getStringExtra("description");
		String ageGroup = intent.getStringExtra("ageGroup");
		int image = intent.getIntExtra("Image", 0);

		

		// oragnisedUsers = oragnisedUserId(group_id,userJoint,joined_user);
		// joined_user = oragnisedUserId(74,userJoint,joined_user);
		userAdapter adapter = new userAdapter(this, R.layout.user_item,
				joined_user);
		userList = (ListView) findViewById(R.id.userView);
		userList.setAdapter(adapter);

		TextView tv = (TextView) findViewById(R.id.abzy211);
		tv.setText(name);

		TextView tv1 = (TextView) findViewById(R.id.textView2);
		String test = "ID " + String.valueOf(user_id);
		tv1.setText(test);

		TextView tv2 = (TextView) findViewById(R.id.textView3);
		tv2.setText(time);

		TextView tv3 = (TextView) findViewById(R.id.textView4);
		tv3.setText(description);

		TextView tv4 = (TextView) findViewById(R.id.textView5);
		tv4.setText(ageGroup);

		ImageView im = (ImageView) findViewById(R.id.imageView1);
		im.setImageResource(image);

		userList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg) {
				userObject c = joined_user.get(position);

				Intent intent = new Intent(GroupProfile.this, userProfile.class);
				intent.putExtra("userId", c.id);
				intent.putExtra("userName", c.username);
				intent.putExtra("bio", c.bio);
				intent.putExtra("interest", c.interest);
				intent.putExtra("image", c.image);
				startActivity(intent);
			}
		});

	}

	public void joinGroup(View v) {
		
		new JoinedGroup(group_id, user_id).execute();

	}

	@SuppressWarnings("null")
	public List<userObject> oragnisedUserId(int groupId,
			List<userObject> relUsers) {
		List<userObject> oragnisedUsers = relUsers;

		HashSet<Integer> newSet = new HashSet<Integer>();

		for (int i = 0; i < map.size(); i++) {
			if (map.get(i) == groupId) {
				newSet.add(value.get(i));

			}
		}

		

		for (int i = 0; i < oragnisedUsers.size(); i++) {
			if (!newSet.contains(oragnisedUsers.get(i).getIDs())) {
				/*
				Log.d("THIS IS THE TEST:",
						String.valueOf(oragnisedUsers.get(i).getIDs()) + " "
								+ oragnisedUsers.get(i).getUsername());
				Log.d("this is the value", "Wadz:" + "true baby");*/
				oragnisedUsers.remove(i);
				
			}

		}

		for (int i = 0; i < oragnisedUsers.size(); i++) {
			if (!newSet.contains(oragnisedUsers.get(i).getIDs())) {
				oragnisedUsers.remove(i);
			}
		}
		for (int i = 0; i < oragnisedUsers.size(); i++) {
			if (!newSet.contains(oragnisedUsers.get(i).getIDs())) {
				oragnisedUsers.remove(i);
			}
		}
		for (int i = 0; i < oragnisedUsers.size(); i++) {
			if (!newSet.contains(oragnisedUsers.get(i).getIDs())) {
				oragnisedUsers.remove(i);
			}
		}
		
/// remove unknown ids 
	

		return oragnisedUsers;

	}

	class JoinedGroup extends AsyncTask<String, String, String> {

		public String groupId;
		public String userId;

		JoinedGroup(int gId, int uId) {
			this.groupId = String.valueOf(gId);
			this.userId = String.valueOf(uId);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GroupProfile.this);
			pDialog.setMessage("Adding you...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			int success;

			try {

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("group_id", groupId));
				params.add(new BasicNameValuePair("user_id", userId));

				Log.d("request!", "starting");

				// Posting user data to script
				JSONObject json = jsonParser.makeHttpRequest(
						LOGIN_URL_userJoined, "POST", params);

				// full json response
				Log.d("Login attempt", json.toString());

				// json success element
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.d("Sucessfully joined!", json.toString());

					finish();

					return json.getString(TAG_MESSAGE);
				} else {
					Log.d("Login Failure!", json.getString(TAG_MESSAGE));
					return json.getString(TAG_MESSAGE);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once product deleted
			pDialog.dismiss();
			if (file_url != null) {
				Toast.makeText(GroupProfile.this, file_url, Toast.LENGTH_LONG)
						.show();

			}

		}

	}

	private class JsonObjectData extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(params[0]);
			try {
				HttpResponse response = httpclient.execute(httppost);
				jsonResult = inputStreamToString(
						response.getEntity().getContent()).toString();

			}

			catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		private StringBuilder inputStreamToString(InputStream is) {
			String rLine = "";
			StringBuilder answer = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				while ((rLine = rd.readLine()) != null) {
					answer.append(rLine);
				}
			}

			catch (IOException e) {
				// e.printStackTrace();
				Toast.makeText(getApplicationContext(),
						"Error..." + e.toString(), Toast.LENGTH_LONG).show();
			}
			return answer;
		}

		@Override
		protected void onPostExecute(String result) {
			DisplayData();

		}
	}

	public void accessWebService() {
		// create the json data
		JsonObjectData task = new JsonObjectData();
		task.execute(new String[] { url });
	}

	public void DisplayData() {
		joined_user.clear();
		map.clear();
		value.clear();
		try {

			JSONObject jsonResponse = new JSONObject(jsonResult);

			// name of the user group
			JSONArray jsonUserDetails = jsonResponse.optJSONArray("abz");
			for (int i = 0; i < jsonUserDetails.length(); i++) {

				JSONObject jsonChildNode = jsonUserDetails.getJSONObject(i);

				String userName = jsonChildNode.optString("username");
				String password = jsonChildNode.optString("password");
				String bio = jsonChildNode.optString("bio");
				String interest = jsonChildNode.optString("interest");
				int userId = jsonChildNode.optInt("id");

				int maps = jsonChildNode.optInt("group_id");
				int values = jsonChildNode.optInt("user_id");
				// list all the attributes of the group

				// list all the attributes of the group
				if(userId !=0){
				joined_user.add(new userObject(userId, userName,
						R.drawable.california_snow, bio, interest));
				}
				if(maps !=0 && values !=0){
				map.add(maps);
				value.add(values);
				}
				// Log.d("THIS IS THE GANSTA:",String.valueOf(values) );

				// Log.d("this is the value","My beautiful wife:"
				// +String.valueOf(joined_user.get(0).getIDs()));

				 if(Login.getRealUser().equals(userName) &&
				 Login.getRealPass().equals(password)){
				 user_id = userId ;
				 }

			}

		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(),
					"Failed to display data! " + e.toString(),
					Toast.LENGTH_SHORT).show();
		}
		joined_user = oragnisedUserId(group_id, joined_user);
	}

}
