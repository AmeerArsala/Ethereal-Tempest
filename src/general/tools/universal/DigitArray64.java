/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools.universal;

/**
 *
 * @author night
 * 
 * Rules: 
 * The first digit must be a non-zero integer
 * The digits must all be positive or zero, however the int value as a whole can be negative
 * Up to 19 digits available (18 digits rule of thumb)
 */
public class DigitArray64 extends DigitArray<Long> {
    private long arr;
    
    public DigitArray64() {
        length = 0;
    }
    
    public DigitArray64(long digits) {
        arr = digits;
        length = countDigits(arr);
    }
    
    public DigitArray64(byte[] digits) {
        arr = toLong(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray64(short[] digits) {
        arr = toLong(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray64(int[] digits) {
        arr = toLong(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray64(long[] digits) {
        arr = toLong(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray64(DigitArray64 copy) {
        length = copy.length;
        
        if (length != 0) {
            arr = copy.arr;
        }
    }
    
    public long asLong() {
        return arr;
    }
    
    @Override
    public Long getNumber() {
        return arr;
    }
    
    //assumes isDeclared() == true
    @Override
    public byte getDigit(int index) {
        long num = arr;
        
        for (int i = 0; i <= index; i++) {
            long scalar = (long)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i == index) {
                return firstDigit;
            } else {
                //remove firstDigit
                num -= firstDigit * scalar;
            }
        }
        
        return -1;
    }
    
    //assumes isDeclared() == true
    @Override
    public byte[] getDigits(int indexA, int indexB) { // [indexA, indexB)
        byte[] digits = new byte[indexB - indexA];
        
        long num = arr;
        
        for (int i = 0; i < indexB; i++) {
            long scalar = (long)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    //assumes isDeclared() == true
    @Override
    public short[] getShortDigits(int indexA, int indexB) { // [indexA, indexB)
        short[] digits = new short[indexB - indexA];
        
        long num = arr;
        
        for (int i = 0; i < indexB; i++) {
            long scalar = (long)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    //assumes isDeclared() == true
    @Override
    public int[] getIntDigits(int indexA, int indexB) { // [indexA, indexB)
        int[] digits = new int[indexB - indexA];
        
        long num = arr;
        
        for (int i = 0; i < indexB; i++) {
            long scalar = (long)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    //assumes isDeclared() == true
    @Override
    public long[] getLongDigits(int indexA, int indexB) { // [indexA, indexB)
        long[] digits = new long[indexB - indexA];
        
        long num = arr;
        
        for (int i = 0; i < indexB; i++) {
            long scalar = (long)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    public long getDigitsAsLong(int i, int i1) {
        return toLong(getDigits(i, i1));
    }
    
    public long getDigitsAsLong(int i) {
        return toLong(getDigits(i));
    }
    
    @Override
    public DigitArray64 subArray(int i, int i1) {
        return new DigitArray64(getDigits(i, i1));
    }
    
    @Override
    public DigitArray64 subArray(int i) {
        return new DigitArray64(getDigits(i));
    }
    
    @Override
    public void setDigit(int i, byte digit) { //must be a single digit
        long scalar = (long)Math.pow(10, length - 1 - i); // correspondingDegree = length - 1 - i
        
        arr += (digit - getDigit(i)) * scalar;
    }
    
    //returns the new DigitArray length
    public byte setAllDigits(long digits) {
        arr = digits;
        return (length = countDigits(arr));
    }
    
    //returns the new DigitArray length
    public byte insertDigits(int i, long digits) {
        if (i == 0) {
            return unshift(digits);
        }
        
        if (i == length - 1) {
            return push(digits);
        }
        
        int correspondingFollowingDegree = length - 1 - (i + 1);
        long followingScalar = (long)Math.pow(10, correspondingFollowingDegree);
        long followingDigits = arr - ((arr / followingScalar) * followingScalar);
        
        long tailDigits = joinDigits(digits, followingDigits);
        return push(tailDigits);
    }
    
    //adds digits to the end, returns the new DigitArray length
    public byte push(long digits) {
        if (length == 0) {
            return setAllDigits(digits);
        } else {
            int additionalDigitCount = countDigits(digits);
            arr = (arr * ((long)Math.pow(10, additionalDigitCount))) + digits;
            
            return (length += additionalDigitCount);
        }
    }
    
    //adds digits to the beginning, returns the new DigitArray length
    public byte unshift(long digits) {
        if (length == 0) {
            return setAllDigits(digits);
        } else {
            arr = (digits * ((long)Math.pow(10, length - 1))) + arr;
            
            return (length += countDigits(digits));
        }
    }
    
    //removes the last digit and returns it
    @Override
    public byte pop() {
        byte removedDigit = (byte)(arr - ((arr / 10) * 10));
        arr /= 10;
        --length;
        return removedDigit;
    }
    
    //removes the first digit and returns it
    @Override
    public byte shift() {
        long scalar = (long)Math.pow(10, length - 1);
        byte firstDigit = (byte)(arr / scalar);
        
        arr -= firstDigit * scalar;
        --length;
        return firstDigit;
    }
    
    @Override
    public void removeAll(int i, int i1) {
        int numDigitsToRemove = i1 - i;
        if (numDigitsToRemove == 1 && i1 == length) { //if only last value
            pop();
            return;
        }
        
        long part1 = getDigitsAsLong(0, i);
        long part2 = getDigitsAsLong(i1);
        
        arr = joinDigits(part1, part2);
        length -= numDigitsToRemove;
    }
    
    
    //optimized to-the-point log10 for bytes, shorts, ints, longs
    public static byte countDigits(long digits) {
        byte digitCount = 1; //at least 1 digit
        long num = digits / 10;
        while (num != 0) {
            num /= 10;
            ++digitCount;
        }
        
        return digitCount;
    }
    
    public static long joinDigits(long digitsA, long digitsB) {
        byte additionalDigitCount = countDigits(digitsB);
        return (digitsA * ((long)Math.pow(10, additionalDigitCount))) + digitsB;
    }
}
