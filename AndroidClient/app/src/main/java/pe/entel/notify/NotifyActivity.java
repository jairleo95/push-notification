package pe.entel.notify;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.badoo.mobile.util.WeakHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import hp.com.R;


public class NotifyActivity extends AppCompatActivity {
    private static final String         END_POINT = "notify";
    public static final String          WS_SERVER_URL = "ws://"+"172.21.75.16:8090/"+END_POINT;
    private static final String         CLIENT_PREFIX = "Guest";
    private static final String         KEY_DATA = "message";
    private static final String         KEY_PAYLOAD = "data";
    private static final String         TOKEN_USER="qwerty";

    private LayerDrawable bellIcon;

    private final WebSocketConnection   socket = new WebSocketConnection();

    private String                      client;
    private ArrayList<NotifyMessage>      items;
    private NotifyAdapter adapter;
    private EditText                    txtContent;
    private ListView                    lstHistory;
    private WeakHandler                 handler;

    private int notificationIdOne = 111;
    private int numMessagesOne = 0;

    /*save data into mobile*/
    SharedPreferences sharedpreferences;
    public static final String mypreference = "counterNotify";
    public static final String counterNumber = "number";

    public void addNotify(View view){
        /*get new items and set new counter*/
        int countNoti=getCounterNotify()+1;
        setBadgeCount(this, bellIcon, countNoti);
        saveCounterNotify(countNoti);
       Log.d("CounterNotify", String.valueOf(getCounterNotify()));
    }
    private  NotificationCompat.Builder buildNotificationCommon(Context _context,String contentTittke,String contentText) {
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder)
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.abc_ic_menu_share_mtrl_alpha)
                        .setContentTitle(contentTittke).setContentText(contentText);
        ++numMessagesOne;
        mBuilder.setNumber(numMessagesOne);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        mBuilder.setVibrate(pattern);
        mBuilder.setStyle(new NotificationCompat.InboxStyle());
        //LED
        mBuilder.setLights(Color.GREEN, 3000, 3000);
        mBuilder.setAutoCancel(true);
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(notification);
          /*  Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(_context, NotifyActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(_context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NotifyActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notificationIdOne, mBuilder.build());
        return mBuilder;
    }

    public void setBadgeCount(Context context, LayerDrawable icon, Integer ccount) {
        BadgeDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }
        if (ccount!=0){
            buildNotificationCommon(context,"Nuevo mensaje","Message from websocket");
        }
        badge.setCount(ccount);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();inflater.inflate(R.menu.menu_chat, menu);
        MenuItem itemCart = menu.findItem(R.id.action_cart);
        bellIcon = (LayerDrawable) itemCart.getIcon();
        /*get new items and set new counter*/
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        setBadgeCount(this, bellIcon, getCounterNotify());
        return super.onCreateOptionsMenu(menu);
    }

    public int getCounterNotify() {
        int number=0;
        sharedpreferences = getSharedPreferences(mypreference,Context.MODE_PRIVATE);
        if (sharedpreferences.contains(counterNumber)) {
            number = sharedpreferences.getInt(counterNumber,0);
        }
        return number;
    }

    public void saveCounterNotify(Integer number) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(counterNumber, number);
        editor.commit();
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_chat);
                /*init ActionBar*/
                Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
                setSupportActionBar(myToolbar);

                // Make Client ID
                client = CLIENT_PREFIX + System.currentTimeMillis();
                // Handler
                handler = new WeakHandler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        Bundle      bundle;
                NotifyMessage notify;
                JSONArray   history;
                JSONObject  data;
                JSONObject  item;
                bundle = message.getData();

                /*aditional code for test*/
                notify = new NotifyMessage();
                notify.message=bundle.toString();
                items.add(notify);
                buildNotificationCommon(getApplicationContext(),"Nuevo mensaje",bundle.toString());
                /*end test*/
                try {
                    // JSON object
                    data = new JSONObject(bundle.getString(KEY_PAYLOAD));
                    // Evaluate action
                    switch(data.getString(NotifyMessage.KEY_ACTION)) {
                        // Whole history
                        case NotifyMessage.ACTION_HISTORY:
                            history = data.getJSONArray(KEY_DATA);
                            for(int h = 0; h < history.length(); h++) {
                                item = history.getJSONObject(h);
                                notify = new NotifyMessage();
                                notify.client = item.getString(NotifyMessage.KEY_CLIENT);
                                notify.message = item.getString(NotifyMessage.KEY_MESSAGE);
                                items.add(notify);
                            }
                            break;
                        // Single item
                        case NotifyMessage.ACTION_CREATE:
                            item = data.getJSONObject(KEY_DATA);
                            notify = new NotifyMessage();
                            notify.client = item.getString(NotifyMessage.KEY_CLIENT);
                            notify.message = item.getString(NotifyMessage.KEY_MESSAGE);
                            items.add(notify);
                            break;
                        case NotifyMessage.ACTION_NOTIFY:
                            buildNotificationCommon(getApplicationContext(),"Nuevo mensaje",bundle.toString());
                            item = data.getJSONObject(KEY_DATA);
                            notify = new NotifyMessage();
                            notify.client = item.getString(NotifyMessage.KEY_CLIENT);
                            notify.message = item.getString(NotifyMessage.KEY_MESSAGE);
                            items.add(notify);
                            break;
                    }
                } catch(JSONException jsone) {
                    jsone.printStackTrace();
                }
                // Update render
                adapter.notifyDataSetChanged();
                // Scroll to bottom
                // Most recent message
                lstHistory.smoothScrollToPosition(adapter.getCount() - 1);
                return false;
            }
        });

        // List
        items = new ArrayList<>();
        adapter = new NotifyAdapter(this, items);

        lstHistory = (ListView)findViewById(R.id.list_history);
        lstHistory.setAdapter(adapter);
        // Text field
        txtContent = (EditText) findViewById(R.id.edit_message);
        txtContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                NotifyMessage notify;
                // Send
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Message present
                    if(txtContent.getText().toString().trim().length() > 0) {
                        // Build message
                        notify = new NotifyMessage();
                        notify.action = NotifyMessage.ACTION_CREATE;
                        notify.client = client;
                        notify.message = txtContent.getText().toString();
                        // Publish
                        socket.sendTextMessage(notify.toJSON());
                        // Clear field
                        txtContent.setText(null);
                    }
                }
                return false;
            }
        });

        // WebSockets events
        try {
            socket.connect(WS_SERVER_URL, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    NotifyMessage notify;
                    // Debug
                    Log.d("WEBSOCKETS", "Connected to server.");
                    // Message for notify history
                    notify = new NotifyMessage();
                    notify.action = NotifyMessage.ACTION_HISTORY;
                    // Send request
                    socket.sendTextMessage(notify.toJSON());
                }

                @Override
                public void onTextMessage(String payload) {
                    Bundle      bundle;
                    Message     message;
                    Log.d("WEBSOCKETS", payload);
                    /* this code send again the incoming message*/
                    bundle = new Bundle();
                    bundle.putString(KEY_PAYLOAD, payload);
                    message = new Message();
                    message.setData(bundle);
                    handler.sendMessage(message);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d("WEBSOCKETS", "Connection lost.");
                }
            });
        } catch(WebSocketException wse) {
            Log.d("WEBSOCKETS", wse.getMessage());
        }
    }

}
