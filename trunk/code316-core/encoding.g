

class P extends Parser;

// match one-or-more 'name followed by age' pairs


fieldDefinition
    : ( 
    	n:NAME e:EQUAL u:NUM (c:COLON (m:NUM o:OPERATOR)*)?
    	{
    		System.out.println("name: " + n.getText());
    		if ( c != null ) {
	    		System.out.println("colon found: " + c.getText());
    		}
    	}
    )+
    ;
   

class L extends Lexer;

NAME:   ( 'a'..'z' | 'A'..'Z' | '$' | '_')+ 
	;
    
OPERATOR: ('+' | '-' | '*' | '/' | '^' )
	;    
    
DOT : '.'
    ;
       
SEMI : ';' 
	;

COLON: ':' 
	;

EQUAL: '='
	;




//BITDEF: NUM ('B' | 'b')
//	;



    
NUM : ('0'..'9')+ (DOT)? ('0'..'9')*
	;

	

WS  :   (   ' '
        |   '\t'
        |   '\r' '\n' { newline(); }
        |   '\n'      { newline(); }
        )
        {$setType(Token.SKIP);} //ignore this token
    ;

