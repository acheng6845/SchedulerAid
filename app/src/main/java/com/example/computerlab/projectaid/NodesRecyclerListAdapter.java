package com.example.computerlab.projectaid;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by computerlab on 4/19/17.
 */
// because we're not using the same ViewHolder class as the superclass, we use a generic to signify what the class we'll be using
public class NodesRecyclerListAdapter extends RecyclerView.Adapter<NodesRecyclerListAdapter.ViewHolder> {

    //dataset will be a linked list
    private ArrayList<String[]> dataset;

    public NodesRecyclerListAdapter(ArrayList<String[]> dataset) {
        this.dataset = dataset;
    }

    //builds the view, create a new view which is invoked by layout manager
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate layout from Activity parent which has a Context to inflate
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.nodes_path_layout, parent, false);
        //holds the view
        return new ViewHolder(layout);
    }

    // does not override as we are using our own custom ViewHolder rather than RecyclerView.ViewHolder
    public void onBindViewHolder(ViewHolder holder, int position) {
        // adapters don't know how to find view by id, but holder has the layout within it
        TextView earlyStartAndFinishView =
                (TextView) holder.layout.findViewById(R.id.early_start_and_finish_text_view);
        TextView lateStartAndFinishView =
                (TextView) holder.layout.findViewById(R.id.late_start_and_finish_text_view);
        //TextView earlyFinishView = (TextView) holder.layout.findViewById(R.id.early_finish_text_view);
        String name = dataset.get(position)[0];
        String earlyStart = dataset.get(position)[1];
        String lateStart = dataset.get(position)[2];
        String earlyFinish = dataset.get(position)[3];
        String lateFinish = dataset.get(position)[4];
        earlyStartAndFinishView.setText(String.format(Locale.ENGLISH,
                "ES: %s, EF: %s", earlyStart, earlyFinish));
        lateStartAndFinishView.setText(String.format(Locale.ENGLISH,
                "LS: %s, LF: %s", lateStart, lateFinish));
        //earlyFinishView.setText(String.format(Locale.ENGLISH, "EF: %s", earlyFinish));

        Button removeButton = (Button) holder.layout.findViewById(R.id.remove_button);
        removeButton.setText(name);
        //lambda spoils me with scope! Without lambda, you need to store Context and name and pass it to the onClickListener class!
//        removeButton.setOnClickListener(
//                v -> {Toast.makeText(holder.layout.getContext(), name, Toast.LENGTH_SHORT).show();}
//        );
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.layout.getContext(), name, Toast.LENGTH_SHORT).show();
            }
        });
        removeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(holder.layout.getContext());
                dialogBuilder.setMessage("Are you sure you want to delete this node?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                HelperSingleton singleton = HelperSingleton.getInstance(
                                        holder.layout.getContext());
                                //delete node from node table
                                singleton.nodeHelper.deleteNode(dataset.get(holder.getAdapterPosition())[6]);
                                //delete node from project nodes column
                                String[] projectNodes = singleton.projectHelper.
                                        getProject(singleton.projectId)
                                        .get(3).split(",");
                                StringBuilder builder = new StringBuilder();
                                for(String nodeId : projectNodes) {
                                    if (!nodeId.equals(dataset.get(holder.getAdapterPosition())[6]) &&
                                            !nodeId.equals("")) {
                                        builder.append(",").append(nodeId);
                                    }
                                }
                                singleton.projectHelper.deleteNode(singleton.projectId,
                                        builder.toString());
                                //delete node from graph
                                singleton.graph.remove_node_and_merge(
                                        singleton.graph.get_node(
                                                dataset.get(holder.getAdapterPosition())[0]
                                        ));
                                Toast.makeText(holder.layout.getContext(), "Node deleted.", Toast.LENGTH_SHORT).show();
                                holder.layout.getContext().startActivity(new Intent(
                                        holder.layout.getContext(), PathViewerActivity.class
                                ));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).create().show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset != null ? dataset.size() : 0;
    }

    // RecyclerView.ViewHolder is an abstract class w/o constructor
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout layout;

        public ViewHolder(LinearLayout itemView) {
            super(itemView);
            layout = itemView;
        }
    }
}
