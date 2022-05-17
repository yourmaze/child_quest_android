package com.minus30.childquest;


import android.graphics.Bitmap;

class BlogPost {
    String title;
    String caption;
    Bitmap photoBitmap;
    int postId;

    public BlogPost setBlogPost(int postId, String name, String age, Bitmap photoBitmap){
        this.postId = postId;
        this.title = name;
        this.caption = age;
        this.photoBitmap = photoBitmap;

        return this;
    }
}


