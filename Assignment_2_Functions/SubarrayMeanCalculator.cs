using System;

class SubarrayMeanFinder {
    static void Main(string[] args) {
        var input = Array.ConvertAll(Console.ReadLine().Split(' '), int.Parse);
        int arraySize = input[0];
        int numberOfQueries = input[1];

        var arrayValues = Array.ConvertAll(Console.ReadLine().Split(' '), long.Parse);
        long[] prefixSumArray = CalculatePrefixSums(arrayValues, arraySize);

        for (int queryIndex = 0; queryIndex < numberOfQueries; queryIndex++) {
            var queryRange = Array.ConvertAll(Console.ReadLine().Split(' '), int.Parse);
            int startIndex = queryRange[0];
            int endIndex = queryRange[1];

            long meanFloor = CalculateMeanFloor(prefixSumArray, startIndex, endIndex);
            Console.WriteLine(meanFloor);
        }
    }

    static long[] CalculatePrefixSums(long[] arrayValues, int arraySize) {
        long[] prefixSumArray = new long[arraySize + 1];
        for (int i = 1; i <= arraySize; i++) {
            prefixSumArray[i] = prefixSumArray[i - 1] + arrayValues[i - 1];
        }
        return prefixSumArray;
    }

    static long CalculateMeanFloor(long[] prefixSumArray, int startIndex, int endIndex) {
        long subarraySum = prefixSumArray[endIndex] - prefixSumArray[startIndex - 1];
        return subarraySum / (endIndex - startIndex + 1);
    }
}
