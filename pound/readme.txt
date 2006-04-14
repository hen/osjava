quick doc

toggle following with ctrl key, e.g. Ctrl+m = toggle message display
m - character/message display
i - display images
s - play sounds
d - display debug info

IMAGES
if there is directory named "images" in the classpath, then
images will be loaded from that directory and displayed.

If you get an OutOfMemoryError try increasing the heap space.

Images are cached X at a time and the cache is replenished as images
are requested from the source. Images repeat.

