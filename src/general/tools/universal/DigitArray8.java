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
 * Up to 3 digits available (2 digits rule of thumb)
 */
public class DigitArray8 extends DigitArray<Byte> {
    private byte arr;
    
    public DigitArray8() {
        length = 0;
    }
    
    public DigitArray8(byte digits) {
        arr = digits;
        length = countDigits(arr);
    }
    
    public DigitArray8(byte[] digits) {
        arr = toByte(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray8(short[] digits) {
        arr = toByte(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray8(int[] digits) {
        arr = toByte(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray8(long[] digits) {
        arr = toByte(digits);
        length = (byte)digits.length;
    }
    
    public DigitArray8(DigitArray8 copy) {
        length = copy.length;
        
        if (length != 0) {
            arr = copy.arr;
        }
    }
    
    public byte asByte() {
        return arr;
    }
    
    @Override
    public Byte getNumber() {
        return arr;
    }
    
    //assumes isDeclared() == true
    @Override
    public byte getDigit(int index) {
        byte num = arr;
        
        for (int i = 0; i <= index; i++) {
            byte scalar = (byte)Math.pow(10, length - 1 - i);
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
        
        byte num = arr;
        
        for (int i = 0; i < indexB; i++) {
            byte scalar = (byte)Math.pow(10, length - 1 - i);
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
        
        byte num = arr;
        
        for (int i = 0; i < indexB; i++) {
            byte scalar = (byte)Math.pow(10, length - 1 - i);
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
        
        byte num = arr;
        
        for (int i = 0; i < indexB; i++) {
            byte scalar = (byte)Math.pow(10, length - 1 - i);
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
        
        byte num = arr;
        
        for (int i = 0; i < indexB; i++) {
            byte scalar = (byte)Math.pow(10, length - 1 - i);
            byte firstDigit = (byte)(num / scalar);
            
            if (i >= indexA) {
                digits[i - indexA] = firstDigit;
            }
            
            //remove firstDigit
            num -= firstDigit * scalar;
        }
        
        return digits;
    }
    
    public byte getDigitsAsByte(int i, int i1) {
        return toByte(getDigits(i, i1));
    }
    
    public byte getDigitsAsByte(int i) {
        return toByte(getDigits(i));
    }
    
    @Override
    public DigitArray8 subArray(int i, int i1) {
        return new DigitArray8(getDigits(i, i1));
    }
    
    @Override
    public DigitArray8 subArray(int i) {
        return new DigitArray8(getDigits(i));
    }
    
    @Override
    public void setDigit(int i, byte digit) { //must be a single digit
        byte scalar = (byte)Math.pow(10, length - 1 - i); // correspondingDegree = length - 1 - i
        
        arr += (digit - getDigit(i)) * scalar;
    }
    
    //returns the new DigitArray length
    public byte setAllDigits(byte digits) {
        arr = digits;
        return (length = countDigits(arr));
    }
    
    //returns the new DigitArray length
    public byte insertDigits(int i, byte digits) {
        if (i == 0) {
            return unshift(digits);
        }
        
        if (i == length - 1) {
            return push(digits);
        }
        
        int correspondingFollowingDegree = length - 1 - (i + 1);
        byte followingScalar = (byte)Math.pow(10, correspondingFollowingDegree);
        byte followingDigits = (byte)(arr - ((arr / followingScalar) * followingScalar));
        
        byte tailDigits = joinDigits(digits, followingDigits);
        return push(tailDigits);
    }
    
    //adds digits to the end, returns the new DigitArray length
    public byte push(byte digits) {
        if (length == 0) {
            return setAllDigits(digits);
        } else {
            int additionalDigitCount = countDigits(digits);
            arr = (byte)((arr * ((byte)Math.pow(10, additionalDigitCount))) + digits);
            
            return (length += additionalDigitCount);
        }
    }
    
    //adds digits to the beginning, returns the new DigitArray length
    public byte unshift(byte digits) {
        if (length == 0) {
            return setAllDigits(digits);
        } else {
            arr = (byte)((digits * ((byte)Math.pow(10, length - 1))) + arr);
            
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
        
        byte part1 = getDigitsAsByte(0, i);
        byte part2 = getDigitsAsByte(i1);
        
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
    
    public static byte joinDigits(byte digitsA, byte digitsB) {
        byte additionalDigitCount = countDigits(digitsB);
        return (byte)((digitsA * ((byte)Math.pow(10, additionalDigitCount))) + digitsB);
    }
}
