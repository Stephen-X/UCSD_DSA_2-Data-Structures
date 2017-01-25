import java.io.*;
import java.util.*;

class Bracket {
    char type;
    int position;

    Bracket(char type, int position) {
        this.type = type;
        this.position = position;
    }

    boolean match(char c) {
        if (this.type == '[' && c == ']')
            return true;
        if (this.type == '{' && c == '}')
            return true;
        if (this.type == '(' && c == ')')
            return true;
        return false;
    }
    
}

class check_brackets {
    public static void main(String[] args) throws IOException {
        InputStreamReader input_stream = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input_stream);
        String text = reader.readLine();

        Stack<Bracket> opening_brackets_stack = new Stack<Bracket>();
        int errorPointer = 0;  // error pointer is 1-based
        for (int position = 0; position < text.length(); ++position) {
            char next = text.charAt(position);

            if (next == '(' || next == '[' || next == '{')
                opening_brackets_stack.push(new Bracket(next, position+1)); // 1-based indexing

            if (next == ')' || next == ']' || next == '}') {
                if (opening_brackets_stack.empty()) {
                    errorPointer = position + 1;
                    break;
                }
                if (!opening_brackets_stack.peek().match(next)){
                    errorPointer = position + 1;
                    break;
                }
                opening_brackets_stack.pop();
            }
        }

        if (errorPointer > 0) System.out.println(errorPointer);  // closing bracket
        else if (!opening_brackets_stack.empty())
            System.out.println(opening_brackets_stack.pop().position);  // opening bracket
        else System.out.println("Success");
    }
}
