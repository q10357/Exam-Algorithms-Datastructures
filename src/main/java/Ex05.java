import java.util.*;

public class Ex05 {

    HashMap<Integer, String> months = new HashMap<>();
    HashMap<Integer, String> courseNames = new HashMap<>();
    HashMap<Integer, String> courseCodes = new HashMap<>();
    HashMap<Integer, String> grading = new HashMap<>();

    public Ex05(){
        initMonths();
        initCourses();
        initGrading();
    }

    private void initGrading() {
        grading.put(0, "GR");
        grading.put(1, "PF");
        grading.put(2, "CE");
    }

    private void initCourses() {
        courseNames.put(0, "Programming");
        courseCodes.put(0, "PG4200");

        courseNames.put(1, "ArtificialIntelligence");
        courseCodes.put(1, "AI1701");

        courseNames.put(2, "FrontendProgramming");
        courseCodes.put(2, "FP1453");

        courseNames.put(3, "Cybersecurity");
        courseCodes.put(3, "SC1007");

        courseNames.put(4, "DataScience");
        courseCodes.put(4, "DS0112");
    }

    private void initMonths() {
        months.put(1, "JAN");
        months.put(2, "FEB");
        months.put(3, "MAR");
        months.put(4, "APR");
        months.put(5, "MAY");
        months.put(6, "JUN");
        months.put(7, "JUL");
        months.put(8, "AUG");
        months.put(9, "SEP");
        months.put(10, "OCT");
        months.put(11, "NOV");
        months.put(12, "DEC");
    }

    public int month(String mon){
        int result = -1;
        for(Map.Entry<Integer, String> e: months.entrySet()){
            if(e.getValue().equalsIgnoreCase(mon)){
                result = e.getKey();
            }
        }
        return result;
    }
    public int grading(String gr){
        int result = -1;
        for(Map.Entry<Integer, String> e: grading.entrySet()){
            if(e.getValue().equalsIgnoreCase(gr)){
                result = e.getKey();
            }
        }
        return result;
    }

    public int course(String code){
        int result = -1;
        for(Map.Entry<Integer, String> e: courseCodes.entrySet()){
            if(e.getValue().equalsIgnoreCase(code)){
                result = e.getKey();
            }
        }
        return result;
    }

    public byte[] compress(String data){
        BitWriter writer = new BitWriter();

        for(int i = 0; i < data.length(); i++){
            //Starting with course
            char c = data.charAt(i);
            while(c != '-'){
                i++;
                c = data.charAt(i);
            }

            i++;
            String courseCode = "";
            c = data.charAt(i);
            while(c != '/'){
                courseCode += c;
                i++;
                c = data.charAt(i);
            }
            //Writing binary number for course
            writer.write(course(courseCode), 3);
            i++;
            //Year
            String yr = "";
            for(int j = 0; j < 4; j++){
                yr += data.charAt(i + j);
            }
            writer.write(Integer.parseInt(yr), 12);
            i+=5;

            //Month
            String mon = "";
            for(int j = 0; j < 3; j++){
                mon += data.charAt(j + i);
            }
            writer.write(month(mon), 4);
            i+=4;

            //Date
            String date = "";
            for(int j = 0; j < 2; j++){
                date += data.charAt(i + j);
            }
            writer.write(Integer.parseInt(date), 5);
            i+=3;

            //Id
            String id = "";
            for(int j = 0; j < 6; j++){
                id += data.charAt(i + j);
            }
            writer.write(Integer.parseInt(id), 20);
            i+=7;
            //Grading
            String gr = "";
            for(int j = 0; j < 2; j++){
                gr += data.charAt(i + j);
            }
            writer.write(grading(gr), 2);
            //We skip further down the string, as we have the relevant information we need
            c = data.charAt(i);
            while(c != ';'){
                i++;
                c = data.charAt(i);
            }
            i++;
        }

        return writer.extract();
    }

    public String decompress(byte[] data) {
        BitReader reader = new BitReader(data);
        String result = "";

        int entries = (data.length * 8) / 46;

        for(int i = 0; i < entries; i++){
            //Course
            int courseIndex = reader.readInt(3);
            result += courseNames.get(courseIndex);
            result += "-";
            result += courseCodes.get(courseIndex);
            int yr = reader.readInt(12);
            result += "/" + yr;
            //Month
            int monthIndex = reader.readInt(4);
            result += "-" + months.get(monthIndex);
            //Date
            result += "-";
            String date = String.format("%02d", reader.readInt(5));
            result += date;
            //Id
            int uniqueId = reader.readInt(20);
            result += "/" + uniqueId;
            //Grading
            result += "-" + grading.get(reader.readInt(2)) + ". ";
            result += "File: exam-" + courseCodes.get(courseIndex);
            result += "-" + yr;
            result += String.format("%02d", monthIndex) + date;
            result += "-" + uniqueId + ".pdf;\n";
        }
        return result;
    }

    /*
    Both BitWriter and BitReader is added from the Algorithm Github repo
    path: lessons/src/main/java/org/pg4200/les11/BitWriter.java
    path: lessons/src/main/java/org/pg4200/les11/BitReader.java

     */

    class BitWriter {

        /**
         * It contains the actual data, in bytes.
         * Note: there is no primitive type for "bit".
         */
        private List<Byte> data;

        /**
         * We add a new byte to the "data" list each time
         * we have written at least 8 bits.
         * Once we write such 8 bits, the buffer gets added
         * into "data" and then reinitialized.
         */
        private byte buffer;

        /**
         * Index on the buffer, counting how many bits it contains,
         * so from 0 to 8.
         * When it reaches 8, we need to flush the buffer into the
         * "data" list, and create a new buffer.
         */
        private int n;

        /**
         * Keep track of whether this reader is closed.
         * Once closed, we cannot add any more bits to it.
         */
        private boolean closed;


        public BitWriter() {
            buffer = 0;
            n = 0;
            data = new ArrayList<>();
            closed = false;
        }

        private void writeBit(boolean bit) {
            checkClosed();

        /*
            shift current data in the buffer to the left.
            Eg, if I have

            xxxx1011

            (where we ignore the context with x) I ll get

            xxx10110

            where rightmost new introduced value is a 0
         */
            buffer <<= 1;

            if (bit) {
            /*
                if true, we need to add a 1.
                We do this by doing an "or" with 1,
                which will just put a 1 bit on rightmost
                position on the 8 bit buffer.

                For example:

                xxx10110
                00000001
                --------
                xxx10111

             */

                buffer |= 1;
            }
        /*
            Note: if "bit" is false, we would need to add
            a 0 on the rightmost position.
            But that is already done when left-shifting
            with <<= 1
         */

            n++;

            if (n == 8) {
            /*
                The byte "buffer" can hold up to 8 bits.
                So it is time to flush it.
             */
                clearBuffer();
            }
        }

        private void clearBuffer() {
            if (n == 0) {
                //nothing in the buffer
                return;
            }

        /*
            if n!=8, we need "padding".
            for example, consider 5 bits

            xxx10100

            and current bytes in "data" list:

            01001110 11101011

            adding the buffer would create a hole of 3 bits xxx, eg

            01001110 11101011 xxx10100

            so, we need to left-shift of 3 bits

            01001110 11101011 10100xxx

            those 3 xxx would actually be 000.
            But how to determine if this 000 are end noise or actual
            data? For decoding, we need to keep track of
            how many bits we write, eg, 8 + 8 + 5 = 23 in this example.
         */
            buffer <<= (8 - n);

            data.add(buffer);

            n = 0;
            buffer = 0;
        }

        public void write(boolean x) {
            writeBit(x);
        }

        public void write(byte b) {
            writeByte(b & 0xFF);
        }

        public void write(int x) {

        /*
            leftmost byte, out of 4. Given

            x = (first 8 bits)(last 24 bits)

            the right-shift 24 leads to the value

            z = (24 bits of 0 data)(first 8 bits)
         */
            writeByte(x >>> 24);

        /*
            Reading the second byte is more tricky.

            x = (first 8 bits)(second byte)(remaining 16 bits)

            After the 16 bit shifting, I get

            z = (16 bits of 0 data)(first 8 bits) (second byte)

            We need to get rid of the "first 8 bits", and
            we do that by a "and" mask with

            0...011111111

            to get only the rightmost 8 bits in z,
            which are the second byte in x, ie

            00...00 kkkkkkkk wwwwwwww
            00...00 00000000 11111111
            -------------------------
            00...00 00000000 wwwwwwww
         */
            writeByte((x >>> 16) & 0xFF); //2nd byte
            writeByte((x >>> 8) & 0xFF); //3rd byte
            writeByte(x & 0xFF); // 4th, rightmost byte
        }

        /**
         * Write the rightmost nbits of the input integer.
         * The info about the leftmost "32 - nbits" is lost.
         */
        public void write(int x, int nbits) {
            if (nbits <= 0 || nbits > 32) {
                throw new IllegalArgumentException("Invalid number of bits: " + nbits);
            }
            if (nbits == 32) {
                //simple case
                write(x);
                return;
            }

            for(int j = nbits-1; j >=0; j--){
                boolean bit = getBitAt(x, j);
                writeBit(bit);
            }
        }

        /**
         * Position is from right to left.
         */
        protected boolean getBitAt(int x, int position) {
            if (position < 0 || position >= 32) {
                throw new IllegalArgumentException("Invalid position: " + position);
            }

            //make sure the ith position is shifted to position 0
            x = x >> position;

            boolean bit  = (x & 1) > 0;

            return bit;
        }

        public void write(String s) {
            for (int i = 0; i < s.length(); i++) {
                write(s.charAt(i));
            }
        }

        public void write(char c) {
            //chars are 2 bytes in Java, as using UTF-16 encoding
            writeByte((c >>> 8) & 0xFF);
            writeByte(c & 0xFF);
        }

        private void writeByte(int x) {
            assert x >= 0 && x < 256;

            if (n == 0) {
            /*
                simple case, the buffer is empty,
                so we can write directly without
                considering it
             */
                data.add((byte) x);
                return;
            }

        /*
            Tricky case. The buffer has already n bits in it,
            and adding a whole new byte will for sure fill the
            buffer, flush it, and then require a new one.
            So, we just write 1 bit at a time, as the flushing
            is already handled inside the writeBit method
         */

            for (int i = 0; i < 8; i++) {
            /*
                The "& 1" is used to get the rightmost bit, as
                the integer value 1 has 000...0001 bit representation
             */
                boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
                writeBit(bit);
            }
        }

        /**
         * Close the reader, and return its data as an array of bytes
         */
        public byte[] extract() {
            close();
            byte[] result = new byte[data.size()];

            for (int i = 0; i < data.size(); i++) {
                result[i] = data.get(i);
            }

            return result;
        }

        public void close() {
            if (closed) {
                return;
            }
            clearBuffer();
            closed = true;
        }


        private void checkClosed() {
            if (closed) {
                throw new IllegalStateException("Closed");
            }
        }
    }

    class BitReader {

        private final byte[] data;

        /**
         * Number of bits read so far from the data buffer
         */
        private int bits = 0;

        public BitReader(byte[] data) {
            this.data = Objects.requireNonNull(data);
        }

        public byte readByte(){
            if(bits % 8 == 0){
                int i = bits / 8;
                bits += 8;
                return data[i];
            }

            byte tmp = 0;

            for(int j=0; j<8; j++){
                tmp = (byte) (tmp << 1);

                boolean k  = readBoolean();
                if(k){
                    tmp |= 1;
                }
            }

            return tmp;
        }

        public boolean readBoolean(){

            int i = bits / 8;

            if(i >= data.length){
                throw new IllegalStateException("No more data to read");
            }

            byte b = data[i];

            int k = bits % 8;

            bits++;

            return ((b >>> (8 - k - 1)) & 1) == 1;
        }



        public int readInt(){

            int x;

            byte a = readByte();
            x = a & 0xFF;

            byte b = readByte();
            x = x << 8;
            x |= (b & 0xFF);

            byte c = readByte();
            x = x << 8;
            x |= (c & 0xFF);

            byte d = readByte();
            x = x << 8;
            x |= (d & 0xFF);

            return x;
        }

        public int readInt(int nbits){
            if (nbits <= 0 || nbits > 32) {
                throw new IllegalArgumentException("Invalid number of bits: " + nbits);
            }

            int x = 0;
            for(int i=0; i<nbits; i++){

                x <<= 1;
                boolean one = readBoolean();
                if(one){
                    x = x | 1;
                }
            }

            return x;
        }

        public char readChar(){

            int x;

            byte a = readByte();
            x = a & 0xFF;

            byte b = readByte();
            x = x << 8;
            x |= (b & 0xFF);

            return (char) x;
        }
    }
}

