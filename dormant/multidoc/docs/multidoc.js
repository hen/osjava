function load() { 
    for(var i=0; i < arguments.length; i+=2) { 
        parent.frames[arguments[i]].location.href=arguments[i+1]; 
    }
}
