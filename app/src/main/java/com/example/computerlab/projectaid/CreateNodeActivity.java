package com.example.computerlab.projectaid;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.UUID;

public class CreateNodeActivity extends AppCompatActivity {

    private Button createNodeButton, returnToPathsButton;
    private Spinner node0Spinner, node1Spinner, node2Spinner, node3Spinner, node4Spinner,
    node5Spinner, node6Spinner, node7Spinner;
    private ArrayList<String> prerequisiteNodes;
    private EditText nodeNameEditText, daysEditText;
    private static int counter;
    private ImageButton goToProjectActivityButton;

    private static final int CHOOSE_ACCOUNT_TAG = 1;
    public static final int BUTTON_NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_node);

        //type = Facebook, Google, etc.
        //need Permissions: user should be aware if it's accessing anything non-basic.
        //post-Marshmallow: permissions are asked when feature is needed instead of when app d/l.
        //Permission.GET_ACCOUNTS or w/e you need has to be in Manifest!
        //Your Accounts = Part of Contacts!
        //CheckPermission:
        //  ActivityCompat = Helper Class (Compat = Backward Compatible)
        //  Manifest.permission.GET_ACCOUNTS != PERMISSION_GRANTED (i.e., PERMISSION_DENIED) ? DO SOMETHING : GOOD TO GO!
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //requestPermissions(context, String[], number);
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    1
            );
        } else {
            toastAllAccounts();

            //for button

        }

        /*                          START OF CODE                                           */

        /* TO DO LIST
        - Multithreading: Free up main thread/fix lag issues
        - Advanced Path Algorithm
        - Finish Prerequisite Nodes Aesthetics
        - Implement Notifications
        - change duration to DATE TIME
        */
        //SINGLETON
        final HelperSingleton singleton = HelperSingleton.getInstance(this);

        //NODE NAME COUNTER
        counter = singleton.nodeHelper.getAllNodes(singleton.projectId).size();
        //System.out.println(singleton.nodeHelper.getAllNodes());

        // CALENDAR
        //Calendar calendar = Calendar.getInstance();
        //SELECT DURATION ROW

        // NODE NAME EDIT TEXT
        nodeNameEditText = (EditText) findViewById(R.id.node_name_edit_text);
        nodeNameEditText.setText(String.format(Locale.ENGLISH, "Node#%d", counter));

        // DEFAULT CLICKED BUTTON IS MEDIUM
        // DATEPICKER FOR SELECTING FINISH DATE OF NODE
        //datePicker = (DatePicker) findViewById(R.id.datePicker);
        daysEditText = (EditText) findViewById(R.id.days_edit_text);
        if (daysEditText != null) daysEditText.setText("1");

        // SPINNER FOR SELECTING THE PREREQUISITE NODE FROM THE LIST OF NODES
        ArrayList<String> spinnerArrayList = new ArrayList<String>();
        spinnerArrayList.addAll(singleton.nodeHelper.getAllNodes(
                singleton.projectId
        ).keySet());
        spinnerArrayList.add("Null");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>
                (this, R.layout.spinner_textview,
                        spinnerArrayList.toArray(new String[spinnerArrayList.size()]));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int nullPosition = spinnerAdapter.getPosition("Null");
        node0Spinner = (Spinner) findViewById(R.id.node0);
        node1Spinner = (Spinner) findViewById(R.id.node1);
        node2Spinner = (Spinner) findViewById(R.id.node2);
        node3Spinner = (Spinner) findViewById(R.id.node3);
        node4Spinner = (Spinner) findViewById(R.id.node4);
        node5Spinner = (Spinner) findViewById(R.id.node5);
        node6Spinner = (Spinner) findViewById(R.id.node6);
        node7Spinner = (Spinner) findViewById(R.id.node7);
        if (node0Spinner != null) {
            node0Spinner.setAdapter(spinnerAdapter);
            node0Spinner.setSelection(nullPosition);
        }
        if (node1Spinner != null) {
            node1Spinner.setAdapter(spinnerAdapter);
            node1Spinner.setSelection(nullPosition);
        }
        if (node2Spinner != null) {
            node2Spinner.setAdapter(spinnerAdapter);
            node2Spinner.setSelection(nullPosition);
        }
        if (node3Spinner != null) {
            node3Spinner.setAdapter(spinnerAdapter);
            node3Spinner.setSelection(nullPosition);
        }
        if (node4Spinner != null) {
            node4Spinner.setAdapter(spinnerAdapter);
            node4Spinner.setSelection(nullPosition);
        }
        if (node5Spinner != null) {
            node5Spinner.setAdapter(spinnerAdapter);
            node5Spinner.setSelection(nullPosition);
        }
        if (node6Spinner != null) {
            node6Spinner.setAdapter(spinnerAdapter);
            node6Spinner.setSelection(nullPosition);
        }
        if (node7Spinner != null) {
            node7Spinner.setAdapter(spinnerAdapter);
            node7Spinner.setSelection(nullPosition);
        }

        // RETURN TO PATHACTIVITY
        returnToPathsButton = (Button) findViewById(R.id.return_to_path_button);
        if (returnToPathsButton != null) {
            returnToPathsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), PathViewerActivity.class));
                }
            });
//            returnToPathsButton.setOnClickListener(
//                    v -> {
//                        startActivity(new Intent(this, PathViewerActivity.class));
//                    });
        }

        // CREATE NODE
        createNodeButton = (Button) findViewById(R.id.create_node_button);
        if (createNodeButton != null) {
            createNodeButton.setOnClickListener(v -> {
                Thread createNodeThread = new Thread() {
                    public void run() {
                        long duration = Long.parseLong(daysEditText.getText().toString());

                        // temporary setup for testing critical path algorithm before connecting to a database
                        String name = nodeNameEditText.getText().toString();

                        // get node names from Spinners' selected items if the value != "Null"
                        prerequisiteNodes = new ArrayList<String>();
                        prerequisiteNodes.add(node0Spinner.getSelectedItem() != "Null" ?
                                (String)node0Spinner.getSelectedItem() : null);
                        prerequisiteNodes.add(node0Spinner.getSelectedItem() != "Null" ?
                                (String)node1Spinner.getSelectedItem() : null);
                        prerequisiteNodes.add(node0Spinner.getSelectedItem() != "Null" ?
                                (String)node1Spinner.getSelectedItem() : null);
                        prerequisiteNodes.add(node2Spinner.getSelectedItem() != "Null" ?
                                (String)node2Spinner.getSelectedItem() : null);
                        prerequisiteNodes.add(node3Spinner.getSelectedItem() != "Null" ?
                                (String)node3Spinner.getSelectedItem() : null);
                        prerequisiteNodes.add(node4Spinner.getSelectedItem() != "Null" ?
                                (String)node4Spinner.getSelectedItem() : null);
                        prerequisiteNodes.add(node5Spinner.getSelectedItem() != "Null" ?
                                (String)node5Spinner.getSelectedItem() : null);
                        prerequisiteNodes.add(node6Spinner.getSelectedItem() != "Null" ?
                                (String)node6Spinner.getSelectedItem() : null);
                        prerequisiteNodes.add(node7Spinner.getSelectedItem() != "Null" ?
                                (String)node7Spinner.getSelectedItem() : null);

                        //get random id
                        String id = UUID.randomUUID().toString();

                        //check for successful insert
                        boolean successfulNodeInsertion = true;
                        // insert into graph here rather than having to load all nodes from database on every activity create
                        try {
                            singleton.graph.insert_node(duration, name, prerequisiteNodes, id);
                        } catch (IllegalArgumentException e) {
                            successfulNodeInsertion = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Node insertion failed. Reason: Duplicate Name.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        if(successfulNodeInsertion) {
                            String[] parents = new String[9];
                            int index = 0;
                            for (String parentName : prerequisiteNodes) {
                                parents[index] = parentName;
                                index += 1;
                            }
                            // check if for some reason, there aren't 8 prerequisite nodes
                            for (int i = index; i < 9; i++) {
                                parents[i] = null;
                            }
                            singleton.nodeHelper.insertNode(id,
                                    name, duration, parents[0], parents[1], parents[2], parents[3],
                                    parents[4], parents[5], parents[6], parents[7], parents[8]);
                            singleton.projectHelper.updateNodes(singleton.projectId, id);
                        }
                        singleton.close();
                        startActivity(new Intent(getApplicationContext(), PathViewerActivity.class));
                    }
                };
                createNodeThread.start();
            });
        }

        // NOTIFICATION
        /*
        int PENDING_INTENT_ID_CODE = 0;
        Button makeNotificationButton = (Button) findViewById(R.id.return_to_path_button);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (makeNotificationButton != null) {
            makeNotificationButton.setOnClickListener(v -> {
                //builders allow for you to create immutable objects only when you're ready to create.
                Notification.Builder builder = new Notification.Builder(this);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_TEXT, "Action intent!");

                RemoteInput remoteInput = new RemoteInput.Builder("key")
                        .setLabel("xD").build();
                Intent chooserIntent = Intent.createChooser(i, "Choose who to send to");
                //pendingintent can also getBroadcast();
                PendingIntent actionIntent = PendingIntent.getActivity(this, 1, chooserIntent, 0);
                Notification.Action action = new Notification.Action.Builder(Icon.createWithResource(
                        this, R.drawable.common_google_signin_btn_icon_dark
                ), "I am a button", actionIntent)
                        .addRemoteInput(remoteInput)
                        .build();
                builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                        .setContentTitle("My Notficiation")
                        .setContentText("Hello world!")
                        .setContentIntent(PendingIntent.getActivity(this, PENDING_INTENT_ID_CODE,
                                new Intent(this, PathViewerActivity.class),
                                0));
                //pendinging intents allow you to wrap an intent and give it to someone else before permission is obtained
                builder.addAction(action);
                manager.notify(1, builder.build());

            });
        }
        */

        /*
        //builder pattern
        public lass Foo {
            //immutable
            pfs name;
            pfi age ;

            private Foo(name, age) {
                this.name = name;
                this.age = age;
            }
            //static lets us use the class w/o instantiating a Foo class
            public static class Builder {
                private String name;
                private int age;

                //return Builder so we can do new Foo.Builder().setName().setAge().build(), i.e., chaining
                public Builder setName() {};
                public Builder setAge() {};

                public Foo build() { return new Foo(name, age); }
            }
         }
         */

        // RECYCLER VIEW


        // Fragments: Layout -> new layout -> footer_fragment
        // Main Activity: <include> -> footer_fragment

    }

    //Inflating: opens up layout file and draws everything
    //We need to inflate menu layout ourselves
    //Inflating only crashes when id hasn't been inflated onto the app yet.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //don't need to define a whole layout; just need what menu options we want
        //create Android Resource Directory, menu
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;

    }

    //in-class notes
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.main_activity_menu_settings_item:
                Toast.makeText(this, String.format(Locale.ENGLISH, "You clicked %s", item.getItemId()), Toast.LENGTH_SHORT).show();
                break;
            case R.id.main_activity_menu_foo_item:
                break;
            case R.id.main_activity_menu_bar_item:
                break;
            default:
                Log.wtf("Main Activity", String.format(Locale.ENGLISH, "Unknown Settings ID: %s", item.getItemId()));
        }
        return true;
    }

    //in-class notes
    private void toastAllAccounts() {
        try {
            AccountManager accountManager = AccountManager.get(this);
            Account[] myAccounts = accountManager.getAccounts();
            for (Account a : myAccounts) {
                Toast.makeText(this, a.name, Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e){

        }
    }

    //in-class notes
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        toastAllAccounts();
    }

    //in-class notes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CHOOSE_ACCOUNT_TAG :
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    b.getString(AccountManager.KEY_ACCOUNT_NAME);

                } else {
                    Toast.makeText(this, "Chose Nothing!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
