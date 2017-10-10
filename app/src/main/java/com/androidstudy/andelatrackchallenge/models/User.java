package com.androidstudy.andelatrackchallenge.models;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by anonymous on 10/10/17.
 */

@Entity
public class User {
    @Id long id;
    public String name;
    public String image_url;
}
