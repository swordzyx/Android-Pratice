package com.sword.mylibrary.leetcode;
/**
 * https://leetcode.cn/problems/coin-change/
 *
 * 给你一个整数数组 coins ，表示不同面额的硬币；以及一个整数 amount ，表示总金额。计算并返回可以凑成总金额所需的 最少的硬币个数 。如果没有任何一种硬币组合能组成总金额，返回 -1 。
 */


public class CoinChange {
    public final int[] coins1 = new int[]{1, 5, 25, 20};
    public final int amount1 = 40;

    public final int[] coins2 = new int[]{1, 2, 5};
    public final int amount2 = 11;

    public final int[] coins3 = new int[]{2};
    public final int amount3 = 3;


    public final int[] coins4 = new int[]{1,2};
    public final int amount4 = 2;


    /**
     * 动态规划，不使用递归
     *
     * 结果：14 ms 击败 38.02% 使用 Java 的用户
     */
    public int coinChange1(int[] coins, int amount) {
        if (amount == 0) {
            return 0;
        }

        if (amount < 0) {
            return -1;
        }

        int[] results = new int[amount + 1];

        for (int i = 0; i < results.length; i++) {
            int tempResult = Integer.MAX_VALUE;
            for (int coin: coins) {
                if (i < coin) continue;
                if (i == coin) {
                    results[i] = 1;
                    break;
                }
                int prevResult = results[i-coin];
                if (prevResult < 0 || prevResult >= tempResult) continue;
                tempResult = prevResult;
            }

            if (results[i] > 0) {
                continue;
            }

            if (tempResult != Integer.MAX_VALUE) {
                results[i] = tempResult + 1;
            } else if (results[i] <= 0){
                results[i] = -1;
            }
        }

        return results[amount];
    }

    /**
     * 暴力递归
     */
    public int coinChange(int[] coins, int amount) {
        if (amount < 0) {
            return 0;
        }

        int result = Integer.MAX_VALUE;
        //如果要凑成的金额刚好是硬币中的一个，直接返回 1
        for (int coin : coins) {
            if (coin == amount) {
                return 1;
            }
            if (amount < coin) continue;
            int prevResult = coinChange(coins, amount-coin);
            if (prevResult < 0 || prevResult >= result) continue;
            result = prevResult;
        }

        if (result != Integer.MAX_VALUE) {
            return result + 1;
        } else {
            return -1;
        }
    }


    /**
     * 记忆化搜索，用一个数组存储计算过的值
     */
    public int coinChange2(int[] coins, int amount ) {
        //init
        int[] results = new int[amount + 1];
        for (int coin: coins) {
            results[coin] = 1;
        }

        //dp(amount) = min(result(amount-coin)) + 1
        return coins2(results, coins, amount);
    }


    private int coins2(int[] results, int[] coins, int amount) {
        if (amount < 0) {
            return Integer.MAX_VALUE;
        }
        int result = results[amount];
        if (result != 0) {
            return result;
        } else {
            result = Integer.MAX_VALUE;
        }

        for (int coin: coins) {
            if (amount < coin) {
                continue;
            }
            result = Math.min(result, coins2(results, coins, amount-coin));
        }
        if (result == Integer.MAX_VALUE) {
            results[amount] = -1;
        } else {
            results[amount] = result + 1;
        }
        return results[amount];
    }
}
