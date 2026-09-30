import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class BasicAlgorithmsTest {

    @Test
    public void test1() {
        Assertions.assertEquals(1, countSpecialIntegers(new int[]{1, 2, 2, 1}));
        Assertions.assertEquals(2, countSpecialIntegers(new int[]{3, 3, 1, 2, 2, 1}));

        Assertions.assertEquals(8, sumDecoded(new long[]{231}));
        Assertions.assertEquals(1649, sumDecoded(new long[]{2522, 2101}));
        Assertions.assertEquals(44723187, sumDecoded(new long[]{59412}));

        Assertions.assertEquals(3, minOperations(new int[]{5, 6, 10}, 4));
        Assertions.assertEquals(3, minOperations(new int[]{10, 2}, 13));
        Assertions.assertEquals(3, minOperationsV1(new int[]{5, 6, 10}, 4));
        Assertions.assertEquals(3, minOperationsV1(new int[]{10, 2}, 13));
    }

    @Test
    public void testMath(){
        System.out.println(Math.pow(2, 3));
        System.out.println(Math.pow(3, 2));
    }

    // 4040 && 4041
    public int minOperationsV1(int[] nums, int sum) {
        // 分组0 - 1背包变体：容量达到C时最小化价值
        // 1. 二维背包可以通过滚动优化位1维
        // dp[j]: 前i个分组背包容量和达到j的最小操作数(价值)
        int[] dp = new int[sum + 1];
        // 2. 因为要求最小值，不要初始化为-1了，初始化为max/2防治+x溢出
        Arrays.fill(dp, Integer.MAX_VALUE / 2);
        dp[0] = 0;
        for (int num : nums) {
            // 3. 通过移位来循环枚举组内所有的物品
            for (int i = sum; i > 0; --i) {
                // 乘2产生的物品
                for (int a = 0; (num << a) <= i; ++a) {
                    dp[i] = Math.min(dp[i], dp[i - (num << a)] + a);
                }
                // 除2产生的物品
                for (int a = 1; (num >> a) > 0 ;++a) {
                    if ((i - (num >> a)) >= 0)
                        dp[i] = Math.min(dp[i], dp[i - (num >> a)] + a);
                }
            }
        }
        return dp[sum] == Integer.MAX_VALUE / 2 ? -1 : dp[sum];
    }
    // 每个元素可以 *或者/2，子集的求和是否eq sum，返回最小操作数
    public int minOperations(int[] nums, int sum) {
        // 前i个元素的子集组成sum[j]需要的最小操作数
        // dp[i][j] = loop in i: min(dp[i][j], dp[i - 1][j - Set{nums[i]}] + Set_iter(nums[i])) O(n^2) * sum
        // Set{nums[i]} 包含nums[i]生成的所有元素变体
        // 因为不存在负数，所以j 只需要统计1 -- sum即可
        int len = nums.length;
        int[][] dp = new int[len + 1][sum + 1];
        for (int i = 0; i <= len; ++i)
            Arrays.fill(dp[i], -1);
        // init -1表示不可达
        for (int j = 0; j <= len; ++j)
            dp[j][0] = 0;

        List<TwoTuple>[] tuples = new ArrayList[len];
        for (int i = 0; i < len; ++i) {
            // num -> tuple
            tuples[i] = new ArrayList<>();
            int val = nums[i];
            int cnt = 0;
            while (val <= sum) {
                tuples[i].add(new TwoTuple(val, cnt));
                val *= 2;
                cnt++;
            }
            val = nums[i] / 2;
            cnt = 1;
            while (val > 0) {
                if (val <= sum)
                    tuples[i].add(new TwoTuple(val, cnt));
                val /= 2;
                cnt++;
            }
        }

        for (int i = 1; i <= len; ++i) {
            for (int j = 1; j <= sum; ++j) {
                // 先要继承上一轮的
                dp[i][j] = dp[i - 1][j];
                    for (TwoTuple t : tuples[i - 1]) {
                    if (t.key > j)
                        continue;
                    if (dp[i - 1][j - t.key] == -1)
                        continue;
                    if (dp[i][j] == -1) {
                        dp[i][j] = dp[i - 1][j - t.key] + t.val;
                        continue;
                    }
                    //System.out.println(i + "  " + j + "  " + k + "  " + (j - t.key));
                    dp[i][j] = Math.min(dp[i][j], dp[i- 1][j - t.key] + t.val);
                }
            }
        }
       // System.out.println(Arrays.toString(tuples));
        //System.out.println(Arrays.deepToString(dp));

        return dp[len][sum];
    }

    class TwoTuple {
        final int key;
        final int val;
        public TwoTuple(int key, int val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public String toString() {
            return "TwoTuple{" +
                    "key=" + key +
                    ", val=" + val +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TwoTuple twoTuple)) return false;
            return key == twoTuple.key && val == twoTuple.val;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, val);
        }
    }

    // 移位取数 + 安全实现pow (% MOD)
    public int sumDecoded(long[] nums) {
        int MOD = 1_000_000_007;
        long sum = 0L;
        List<Integer> list;
        for (long num : nums) {
            int width = (int)(num % 10);
            long d = num / 10;
            // 如何取d里前两个数、以及拆后两个数，将数据转为list

            list = new ArrayList<>();
            while (d > 0) {
                list.add((int)(d % 10));
                d /= 10;
            }
            Collections.reverse(list);
            // System.out.println("kk: " + list);
            int r = Math.min(list.size(), width);
            long a = 0L;
            long b = 0L;
            for (int i = 0; i < r; ++i) {
                a = a * 10 + list.get(i);
            }
            for (int i = r; i < list.size(); ++i) {
                b = b * 10 + list.get(i);
            }
            //System.out.println(a + "  " + b + "  " + r);
            sum = (sum + ((long)pow(a, b)) % MOD) % MOD;
        }
        return (int) sum;
    }

    private long pow(long x, long n) {
        int MOD = 1_000_000_007;
        long res = 1;
        for (; n > 0; n /= 2) {
            if (n % 2 > 0) {
                res = res * x % MOD;
            }
            x = x * x % MOD;
        }
        return res;
    }

    public int countSpecialIntegers(int[] nums) {
        Map<Integer, Boolean> map = new HashMap<>();
        map.put(nums[0], true);
        for (int i = 1, len = nums.length; i < len; ++i) {
            if (nums[i] == nums[i - 1])
                continue;
            map.put(nums[i], !map.containsKey(nums[i]));
        }
        return (int) map.entrySet().stream()
                .filter(Map.Entry::getValue)
                .count();
    }
}
