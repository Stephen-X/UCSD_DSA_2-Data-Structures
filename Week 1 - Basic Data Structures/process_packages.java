import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayDeque;

class Request {
    public Request(int arrival_time, int process_time) {
        this.arrival_time = arrival_time;
        this.process_time = process_time;
    }

    public int arrival_time;
    public int process_time;
}

class Response {
    public Response(boolean dropped, int start_time) {
        this.dropped = dropped;
        this.start_time = start_time;
    }

    public boolean dropped;
    public int start_time;
}

class Buffer {
    public Buffer(int size) {
        this.size_ = size;
        this.finish_time_ = new ArrayDeque<Integer>();
    }

    // process network packet requests one at a time 
    public Response Process(Request request) {
        if (size_ <= 0) return new Response(true, -1); // 0-sized buffer specified by user

        if (finish_time_.isEmpty()) {  // buffer is empty
            finish_time_.add(request.arrival_time + request.process_time);
            return new Response(false, request.arrival_time);
        }

        int lastFinishTime = finish_time_.getLast(); // finish time of the last packet in queue

        while (!finish_time_.isEmpty() && finish_time_.getFirst() <= request.arrival_time)
            finish_time_.removeFirst();
        // remove packets already processed by the time current packet arrives
        
        if (finish_time_.size() >= size_)
            return new Response(true, -1); // buffer is full; packet dropped
        
        if (request.arrival_time >= lastFinishTime) {
            // the previous packets are done processing by the time the current packet arrives
            finish_time_.add(request.arrival_time + request.process_time);
            return new Response(false, request.arrival_time);
        } else { // the current packet should be waiting in buffer
            finish_time_.add(lastFinishTime + request.process_time);
            return new Response(false, lastFinishTime);
        }
    }

    private int size_;
    private ArrayDeque<Integer> finish_time_;
}

// class for processing incoming network packages
class process_packages {
    private static ArrayList<Request> ReadQueries(Scanner scanner) throws IOException {
        int requests_count = scanner.nextInt();  // n of incoming network packages
        ArrayList<Request> requests = new ArrayList<Request>();
        for (int i = 0; i < requests_count; ++i) {
            int arrival_time = scanner.nextInt();
            int process_time = scanner.nextInt();
            requests.add(new Request(arrival_time, process_time));
        }
        return requests;
    }

    private static ArrayList<Response> ProcessRequests(ArrayList<Request> requests, Buffer buffer) {
        ArrayList<Response> responses = new ArrayList<Response>();
        for (int i = 0; i < requests.size(); ++i) {
            responses.add(buffer.Process(requests.get(i)));
        }
        return responses;
    }

    private static void PrintResponses(ArrayList<Response> responses) {
        for (int i = 0; i < responses.size(); ++i) {
            Response response = responses.get(i);
            if (response.dropped) {  // the packet is dropped
                System.out.println(-1);
            } else {
                System.out.println(response.start_time);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        int buffer_max_size = scanner.nextInt();
        Buffer buffer = new Buffer(buffer_max_size); // network buffer of fixed size S

        ArrayList<Request> requests = ReadQueries(scanner);
        ArrayList<Response> responses = ProcessRequests(requests, buffer);
        PrintResponses(responses);
    }
}
