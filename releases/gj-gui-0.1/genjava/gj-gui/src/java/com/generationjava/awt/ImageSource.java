package com.generationjava.awt;

import java.awt.Image;

public interface ImageSource {

    public Image getImage();
    
    // has the image changed, ie) we should stop caching
    // this is either of two things.
    // 1) The image's variables have changed and it 
    //     needs regenerating.
    // 2) It's been regenerated, but the user of this 
    //     source has yet to  
    //public boolean isModified(Image img);

}