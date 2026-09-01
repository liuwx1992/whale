package com.zoo.whale.leetcode.array;

/**
 * @Date: 2026年 02月 27日
 * @Time:17时:17分
 * @Author: weixin.liu
 * @Version:V1.0
 * 因为给定数组是有序的，所以相同元素必然连续。我们可以使用双指针解决本题，遍历数组检查每一个元素是否应该被保留，如果应该被保留，就将其移动到指定位置。
 * 具体地，我们定义两个指针 slow 和 fast 分别为慢指针和快指针，其中慢指针表示处理出的数组的长度，快指针表示已经检查过的数组的长度，
 * 即 nums[fast] 表示待检查的第一个元素，nums[slow−1] 为上一个应该被保留的元素所移动到的指定位置。
 *
 * 因为本题要求相同元素最多出现两次而非一次，所以我们需要检查上上个应该被保留的元素 nums[slow−2]
 * 是否和当前待检查元素 nums[fast] 相同。当且仅当 nums[slow−2]=nums[fast] 时，当前待检查元素 nums[fast]
 * 不应该被保留（因为此时必然有 nums[slow−2]=nums[slow−1]=nums[fast]）。最后，slow 即为处理好的数组的长度。
 *
 * 特别地，数组的前两个数必然可以被保留，因此对于长度不超过 2 的数组，我们无需进行任何处理，对于长度超过 2 的数组，我们直接将双指针的初始值设为 2 即可。
 *
 * 作者：力扣官方题解
 * 链接：https://leetcode.cn/problems/remove-duplicates-from-sorted-array-ii/solutions/702644/shan-chu-pai-xu-shu-zu-zhong-de-zhong-fu-yec2/
 * 来源：力扣（LeetCode）
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */
public class RemoveDuplicates {

    public static void main(String[] args) {
        int[] array = new int[]{1,1,1,2,2,3,3,4,4,5,6};
//        int[] array = new int[]{1,1,1,1,1,1};
        System.out.println(removeDuplicates(array));
     }



    public static int removeDuplicates(int[] nums) {
        int length = nums.length;
        if (length <= 2) {
            return length;
        }
        int s = 2;
        int f = 2;
        while (f < length) {
            int slow = nums[s - 2];
            int fast = nums[f];

            //System.out.println(f - s + 2);

            if(slow != fast){
                nums[s] = nums[f];
                s++;
            }

            for (int ss : nums) {
                System.out.print(ss + " ");
            }
            System.out.println();
            f++;
        }
        return s;
    }

}
