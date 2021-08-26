/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.tools.universal;

/**
 *
 * @author night
 * Rules: 
 * The first digit must be a non-zero integer
 * The digits must all be positive or zero, however the int value as a whole can be negative
 * Up to 9 digits available (8 digits rule of thumb)
 */
public class DigitArray32 extends DigitArray<Integer> {
    private int arr;
    
    public DigitArray32() {
        length = 0;
    }
    
    public DigitArray32(int digits) {
        arr = digits;
        length = countDigits(arr);
    }
    
    public DigitArray32(byte[] digits) {
        arr = toInt(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray32(short[] digits) {
        arr = toInt(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray32(int[] digits) {
        arr = toInt(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray32(long[] digits) {
        arr = toInt(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray32(DigitArray32 copy) {
        length = copy.length;
        
        if (length != 0) {
            arr = copy.arr;
        }
    }
    
    public int asInt() {
        return arr;
    }
    
    @Override
    public Integer getNumber() {
        return arr;
    }
    
    //assumes isDeclared() == true
    @Override
    public byte getDigit(int index) {
        int num = arr;
        
        for (int i = 0; i <= index; i++) {
            int scalar = (int)Math.pow(10, length - 1 - i);
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
        
        int num = arr;
        
        for (int i = 0; i < indexB; i++) {
            int scalar = (int)Math.pow(10, length - 1 - i);
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
        
        int num = arr;
        
        for (int i = 0; i < indexB; i++) {
            int scalar = (int)Math.pow(10, length - 1 - i);
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
        
        int num = arr;
        
        for (int i = 0; i < indexB; i++) {
            int scalar = (int)Math.pow(10, length - 1 - i);
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
        
        int num = arr;
        
        for (int i = 0; i < indexB; i++) {
            int scalar = (int)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    public int getDigitsAsInt(int i, int i1) {
        return toInt(getDigits(i, i1));
    }
    
    public int getDigitsAsInt(int i) {
        return toInt(getDigits(i));
    }
    
    @Override
    public DigitArray32 subArray(int i, int i1) {
        return new DigitArray32(getDigits(i, i1));
    }
    
    @Override
    public DigitArray32 subArray(int i) {
        return new DigitArray32(getDigits(i));
    }
    
    @Override
    public void setDigit(int i, byte digit) { //must be a single digit
        int scalar = (int)Math.pow(10, length - 1 - i); // correspondingDegree = length - 1 - i
        
        arr += (digit - getDigit(i)) * scalar;
    }
    
    //returns the new DigitArray length
    public byte setAllDigits(int digits) {
        arr = digits;
        return (length = countDigits(arr));
    }
    
    //returns the new DigitArray length
    public byte insertDigits(int i, int digits) {
        if (i == 0) {
            return unshift(digits);
        }
        
        if (i == length - 1) {
            return push(digits);
        }
        
        int correspondingFollowingDegree = length - 1 - (i + 1);
        int followingScalar = (int)Math.pow(10, correspondingFollowingDegree);
        int followingDigits = arr - ((arr / followingScalar) * followingScalar);
        
        int tailDigits = joinDigits(digits, followingDigits);
        return push(tailDigits);
    }
    
    //adds digits to the end, returns the new DigitArray length
    public byte push(int digits) {
        if (length == 0) {
            return setAllDigits(digits);
        } else {
            int additionalDigitCount = countDigits(digits);
            arr = (arr * ((int)Math.pow(10, additionalDigitCount))) + digits;
            
            return (length += additionalDigitCount);
        }
    }
    
    //adds digits to the beginning, returns the new DigitArray length
    public byte unshift(int digits) {
        if (length == 0) {
            return setAllDigits(digits);
        } else {
            arr = (digits * ((int)Math.pow(10, length - 1))) + arr;
            
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
        int scalar = (int)Math.pow(10, length - 1);
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
        
        int part1 = getDigitsAsInt(0, i);
        int part2 = getDigitsAsInt(i1);
        
        arr = joinDigits(part1, part2);
        length -= numDigitsToRemove;
    }
    
    
    //optimized to-the-point log10 for bytes, shorts, ints, longs
    public static byte countDigits(int digits) {
        byte digitCount = 1; //at least 1 digit
        int num = digits / 10;
        while (num != 0) {
            num /= 10;
            ++digitCount;
        }
        
        return digitCount;
    }
    
    public static int joinDigits(int digitsA, int digitsB) {
        byte additionalDigitCount = countDigits(digitsB);
        return (digitsA * ((int)Math.pow(10, additionalDigitCount))) + digitsB;
    }
}
