package com.example.angela.stylistpage;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Rating;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.angela.stylistpage.models.StylistModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static Button button_sbn;
    private static RatingBar rating_b;
    //private static TextView text_v;



    private TextView OurStylist;
    private ListView stylists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListenerForRatingBar();
        onButtonClickListener();

        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        stylists = (ListView)findViewById(R.id.stylists);

 //       new JSONTask().execute("file:///C:/Users/angela/Desktop/stylist.htm");
    }

    public void ListenerForRatingBar(){
        rating_b =(RatingBar)findViewById(R.id.ratingBar);
        //text_v (TextView)findViewById(R.id.textView);

        rating_b.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        //text_V.setText(Strig.valueOf(rating));
                    }
                }
        );
    }

    public void onButtonClickListener(){
        rating_b = (RatingBar) findViewById(R.id.ratingBar);
        button_sbn = (Button)findViewById(R.id.submitBtn);

        button_sbn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     //   Toast.makeText(getApplicationContext(),rating_b.getNumStars(),Toast.LENGTH_SHORT.show();
                }
                }
        );
    }

    public class JSONTask extends AsyncTask<String, String, List<StylistModel>> {

        @Override
        protected List<StylistModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("stylist");


                List<StylistModel> stylistModelList = new ArrayList<>();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    StylistModel stylistModel = new StylistModel();
                    stylistModel.setStylistName(finalObject.getString("stylistName"));
                    stylistModel.setStylistRating(finalObject.getInt("stylistRating"));
                    stylistModel.setCaseFinished(finalObject.getInt("caseFinished"));
                    stylistModel.setStylistIcon(finalObject.getString("stylistIcon"));

                    //adding the final object to the list
                    stylistModelList.add(stylistModel);
                }
                return stylistModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<StylistModel> result) {
            super.onPostExecute(result);

            StylistAdapter adapter = new StylistAdapter(getApplicationContext(), R.layout.row, result);
            stylists.setAdapter(adapter);
            //ToDO need to set data to the list
        }
    }

    public class StylistAdapter extends ArrayAdapter{

        private List<StylistModel> stylistModelList;
        private int resource;
        public LayoutInflater inflater;
        public StylistAdapter(Context context, int resource, List objects){
            super(context, resource, objects);
            stylistModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){

            ViewHolder holder = null;

            if(convertView == null){
                convertView = inflater.inflate(resource, null);
                holder.proStylistIcon = (ImageView)findViewById(R.id.proStylistIcon);
                holder.proStylistName = (TextView)convertView.findViewById(R.id.proStylistName);
                holder.proStylistRating = (RatingBar)convertView.findViewById(R.id.proStylistRating);
                holder.proCaseFinished = (TextView)findViewById(R.id.proCaseFinished);
                holder.proStylistName.setText(stylistModelList.get(position).getStylistName());
                holder.proCaseFinished.setText("Case Finished: " + stylistModelList.get(position).getCaseFinished());
            convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }



            final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

            // Then later, when you want to display image
            ImageLoader.getInstance().displayImage(stylistModelList.get(position).getStylistIcon(), holder.proStylistIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            holder.proStylistName.setText(stylistModelList.get(position).getStylistName());
            holder.proCaseFinished.setText(stylistModelList.get(position).getCaseFinished());

            //rating bar
            holder.proStylistRating.setRating(stylistModelList.get(position).getStylistRating()/2);
            return convertView;
        }

        class ViewHolder{
            private ImageView proStylistIcon;
            private TextView proStylistName;
            private RatingBar proStylistRating;
            private TextView proCaseFinished;
        }
    }


}