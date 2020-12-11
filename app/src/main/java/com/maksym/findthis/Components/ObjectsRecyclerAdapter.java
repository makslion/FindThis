package com.maksym.findthis.Components;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maksym.findthis.Database.ObjectEntity;
import com.maksym.findthis.ObjectDetails;
import com.maksym.findthis.R;
import com.maksym.findthis.Utils.Constants;

import java.util.List;

public class ObjectsRecyclerAdapter extends RecyclerView.Adapter <ObjectsRecyclerAdapter.ObjectViewHolder>
{
    private String TAG = getClass().getSimpleName();

    static class ObjectViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView objectName;

        private ObjectViewHolder(View itemView)
        {
            super(itemView);
            objectName = itemView.findViewById(R.id.objectNameLItextView);
        }
    }


    private final LayoutInflater inflater;

    private List<ObjectEntity> objects; // Cached copy of words

    public ObjectsRecyclerAdapter(Context context)
    {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ObjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Log.d(TAG,"on create view holder");

        View itemView = inflater.inflate(R.layout.collection_list_item, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
//                TextView taskName = v.findViewById(R.id.taskNameLisItem);
//                Intent it = new Intent(v.getContext(), Task.class);
//                it.putExtra(Constants.EXTRA_TASK_NAME, taskName.getText());
//                v.getContext().startActivity(it);

                TextView objectNameView = v.findViewById(R.id.objectNameLItextView);
                String objectName = (String) objectNameView.getText();
                ObjectEntity objectToPass = null;

                for (ObjectEntity object : objects)
                    if (object.getObjectName().equals(objectName))
                        objectToPass = object;

                Intent intent = new Intent(v.getContext(), ObjectDetails.class);
                intent.putExtra(Constants.EXTRA_OBJECT, objectToPass);
                v.getContext().startActivity(intent);
            }
        });

        return new ObjectViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(ObjectViewHolder holder, int position)
    {
        Log.d(TAG,"on bind view holder");

        if (objects.size() > 0) {
            ObjectEntity current = objects.get(position);
            holder.objectName.setText(current.getObjectName());
        }
        else
            holder.objectName.setText("No objects so far");
    }

    public void setObjects(List<ObjectEntity> objects)
    {
        this.objects = objects;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount()
    {
        Log.d(TAG,"getItemCount()");
        return objects == null ? 0 : objects.size();
    }
}