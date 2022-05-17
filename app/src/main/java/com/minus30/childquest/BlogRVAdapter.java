package com.minus30.childquest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.minus30.childquest.R;

import java.util.List;

public class BlogRVAdapter extends RecyclerView.Adapter<BlogRVAdapter.BlogPostViewHolder> {

    public static class BlogPostViewHolder extends RecyclerView.ViewHolder  {

        CardView blogPost;
        TextView postTitle;
        TextView postCaption;
        ImageView postImage;

        BlogPostViewHolder(View itemView) {
            super(itemView);
            blogPost = (CardView)itemView.findViewById(R.id.blogPost);
            postTitle = (TextView)itemView.findViewById(R.id.blogPostTitle);
            postCaption = (TextView)itemView.findViewById(R.id.blogPostCaption);
            postImage = (ImageView)itemView.findViewById(R.id.blogPostImage);
        }
    }


    List<BlogPost> persons;

    BlogRVAdapter(List<BlogPost> persons){
        this.persons = persons;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public BlogPostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.blog_loop, viewGroup, false);
        BlogPostViewHolder pvh = new BlogPostViewHolder(v);

        return pvh;
    }

    @Override
    public void onBindViewHolder(BlogPostViewHolder blogPostViewHolder, int i) {
        blogPostViewHolder.blogPost.setTag(persons.get(i));
        blogPostViewHolder.postTitle.setText(persons.get(i).title);
        blogPostViewHolder.postCaption.setText(persons.get(i).caption);
        blogPostViewHolder.postImage.setImageBitmap(persons.get(i).photoBitmap);
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }
}