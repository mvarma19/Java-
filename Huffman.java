import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
/*
we will be using a priority queue to build the tree
Huffman encodng is optimal encoding for compression
 */

public class Huffman {
    private static final int ALPHABET_SIZE = 256;

    /*
    compress method which returns String data read from file
     */
    public HuffmanResult compress(final String data) {
        final int[] frequency = buildfreqtable(data);
        final Node root=HT(frequency);
        final Map<Character,String> lookupTable=buildLookupTable(root);

        return new HuffmanResult(printData(data,lookupTable),root);
    }
    static class HuffmanResult {
        final String secretdata;
        final Node root;
        HuffmanResult(final String secretdata,final Node root)
        {
            this.root=root;
            this.secretdata=secretdata;
        }



    }
    /*
    the below method will help us get the binary string for the particular
    file
     */

    private static String printData(final String data,final Map<Character, String> lookupTable)
    {
        final StringBuilder stringBuilder=new StringBuilder();
        for(final char character:data.toCharArray())
        {
            stringBuilder.append(lookupTable.get(character));
        }
        return stringBuilder.toString();

    }

    /*
    map each character to a binary encoding and lenght of the binary code
    will depend on how frequently the character occurs in the file
    if a character is more frequent in out data, the file then our binary code
    will be shorter
     */
    /*
    its going to be  RECURSIVE ALGORTIHM
     */
    private static Map<Character,String> buildLookupTable(final Node root)
    {
        final Map<Character,String> lookupTable=new HashMap<>();
        buildLookupTableImpl(root,"",lookupTable);

        return lookupTable;

    }

    private static void buildLookupTableImpl(Node node, String s,
                                             Map<Character, String> lookupTable)
            /*
            any time we traverse the left of the tree we get a 0 and any tme
            we traverse right of the tree we get 1.
             */
    {
        if(!node.isLeaf())
        {
            buildLookupTableImpl(node.LC,s+'0',lookupTable);
            buildLookupTableImpl(node.RC,s+'1',lookupTable);
        }
        else
        {
            lookupTable.put(node.character,s);
        }

    }

    /*
    second thing in the program we need to build a huffman tree
    we first start by stoting each character with its weight
    and then merging two least occuring frequices and repeat this
    process.
    we will use priority queue. Node will be thr root of our tree
     */
    private static Node HT(int[] freq)
        {
            final PriorityQueue<Node>priorityQueue=new PriorityQueue<>();
            for(char i=0;i<ALPHABET_SIZE;i++)
            {
                if(freq[i]>0)
                {
                    priorityQueue.add(new Node(i,freq[i],null,null));
                }
            }
            if(priorityQueue.size()==1)
            {
                priorityQueue.add(new Node('\0',1,null,null));
            }
            /*
            this loop will go on until we reach the root of the node
            and hence size is greater than one
             */
            while(priorityQueue.size()>1)
            {
                /*
                taking two characters from the queue
                 */
                final Node L=priorityQueue.poll();
                final Node R=priorityQueue.poll();
                /*
                here we merge into a new node
                 */
                final Node parent=new Node('\0',L.frequency+R.frequency,L,R);
                /*
                Now we just gotta add the parent in the priority queue
                 */
                priorityQueue.add(parent);
            }
            /*
            we will returb priorityqueue.poll so that we get the root node
             */
   return priorityQueue.poll();
        }


        /*
        we need frequency table to bild the above tree
        and we also need to make them comparable based on
        their frequency and hence we are going to make Node
        implement Comparable interface

         */
        static class Node implements Comparable<Node>
        {
            private final char character;
            private final int frequency;
            private final Node LC;
            private final Node RC;
            private Node(final char character,final int frequency
            ,final Node LC,final Node RC)
            {
                this.character=character;
                this.frequency=frequency;
                this.LC=LC;
                this.RC=RC;

            }
            /*
            we will consider case if there is no children to
            a particular Parent Node.
            */
            boolean isLeaf()
            {
                return this.LC==null && this.RC==null;
            }

/*
way to compare nodes
 */
            @Override
            public int compareTo(Node other) {
                final int FreqCompare=Integer.compare(this.frequency,other.frequency);
                if(FreqCompare!=0)
                {
                    return FreqCompare;
                }

                return Integer.compare(this.character,other.character);
            }
        }

    private static int[] buildfreqtable(final String data) {
        /*
         first step we need to do is we need to count frequency
         of each character in the file, we have passed alphabet size
         as we have considered ascii values of each character
         */
        final int[] frequency = new int[10000];
        for (final char character : data.toCharArray()) {
            /*
            frequency of that character is going to be incremented
             */
            frequency[character]++;
        }

        return frequency;
    }



    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("No arguments specified!");
        }
        if (args.length == 1) {
            FileReader fileReader = new FileReader(args[0]);


            String fileContents = "";

            int i;

            while ((i = fileReader.read()) != -1) {
                char ch = (char) i;

                fileContents = fileContents + ch;

            }

            final int[] ft = buildfreqtable(fileContents);
            final Node n=HT(ft);
            final Map<Character,String> lookup=buildLookupTable(n);
            final Huffman secret=new Huffman();
            final HuffmanResult result=secret.compress(fileContents);
            System.out.println("encoded message="+result.secretdata);



        }
    }
}
