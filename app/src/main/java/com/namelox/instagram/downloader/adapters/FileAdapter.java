package com.namelox.instagram.downloader.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.namelox.instagram.downloader.R;
import com.namelox.instagram.downloader.activities.VideoPlayerActivity;
import com.namelox.instagram.downloader.listener.FileClickInterface;

import java.io.File;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    public Context context;
    public List<File> fileArrayList;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");
    public FileClickInterface fileListClickInterface;

    public FileAdapter(Context context, List<File> fileArrayList, FileClickInterface fileListClickInterface) {
        this.context = context;
        this.fileArrayList = fileArrayList;
        this.fileListClickInterface=  fileListClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_downloaded, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        File fileItem = fileArrayList.get(i);
        String format = new SimpleDateFormat("dd-MM-yyyy  hh:mm a").format(new Date(fileItem.lastModified()));
        viewHolder.textViewName.setText(fileArrayList.get(i).getName());
        viewHolder.textViewTime.setText(format);

        try {
            String extension = fileItem.getName().substring(fileItem.getName().lastIndexOf("."));
            if (extension.equals(".mp4")) {
                viewHolder.imageViewPlay.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imageViewPlay.setVisibility(View.GONE);
            }
            Glide.with(context).load(fileItem.getPath()).into(viewHolder.imageViewCover);
        }catch (Exception ex){
        }

        viewHolder.imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUp(view, fileArrayList.get(i));
            }
        });

        viewHolder.relativeLayoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileListClickInterface.getPosition(i,fileItem);
            }
        });

        viewHolder.imageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("PathVideo", fileItem.getPath().toString());
                context.startActivity(intent);
            }
        });

    }

    private void popUp(View view, final File file) {
        final PopupMenu popup = new PopupMenu(context.getApplicationContext(), view);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int i = item.getItemId();
                if (i == R.id.action_delete) {
                    if (file.delete()) {
                        updateList();
                        //fileArrayList.remove(i);
                        //notifyDataSetChanged();
                        //Utils.setToast(context, context.getResources().getString(R.string.file_deleted));
                    }
                    return true;
                }
                else if (i == R.id.action_share) {
                    shareFile(file);
                    return true;
                }
                else {
                    return onMenuItemClick(item);
                }
            }
        });
        popup.show();
    }

    public void updateList() {
        File videoFile = new File(Environment.getExternalStoragePublicDirectory(context.getResources().getString(R.string.app_name)).getAbsolutePath());
        if (videoFile.exists()) {
            List<File> nonExistentFiles = new ArrayList<>();
            nonExistentFiles.addAll(Arrays.asList(videoFile.listFiles()));
            this.fileArrayList = nonExistentFiles;
            notifyDataSetChanged();
        }
    }

    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://" +file.getAbsolutePath()));

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        context.startActivity(Intent.createChooser(intentShareFile, "Share File"));

    }

    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout relativeLayoutContent;
        public ImageView imageViewCover;
        public ImageView imageViewPlay;
        public ImageView imageViewMenu;
        public TextView textViewName;
        public TextView textViewTime;

        public ViewHolder(View view) {
            super(view);
            this.relativeLayoutContent = view.findViewById(R.id.relativeLayoutContent);
            this.imageViewCover = view.findViewById(R.id.imageViewCover);
            this.imageViewPlay = view.findViewById(R.id.imageViewPlay);
            this.imageViewMenu = view.findViewById(R.id.imageViewMenu);
            this.textViewName = view.findViewById(R.id.textViewName);
            this.textViewTime = view.findViewById(R.id.textViewTime);
        }
    }

}