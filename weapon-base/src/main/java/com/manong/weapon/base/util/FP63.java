package com.manong.weapon.base.util;

/**
 * 将任意字符串映射为long值类型，保证很小的重复率
 *
 * @author frankcl
 * @create 2019-05-27 15:29
 */
public class FP63 {

    private final static long X63 = 0x1L;

    private final static long IRRED_POLY = 0xa8f9c165a4295d90L;

    private final static long[] byteModeTable7 = { 0x0L, 0x2b6f83dba24205dfL, 0x56df07b744840bbeL, 0x7db0846ce6c60e61L,
            0x62ef71284ec67e63L, 0x4980f2f3ec847bbcL, 0x3430769f0a4275ddL, 0x1f5ff544a8007002L, 0xa8f9c165a4295d9L,
            0x21e01fcdf8009006L, 0x5c509ba11ec69e67L, 0x773f187abc849bb8L, 0x6860ed3e1484ebbaL, 0x430f6ee5b6c6ee65L,
            0x3ebfea895000e004L, 0x15d06952f242e5dbL, 0x151f382cb4852bb2L, 0x3e70bbf716c72e6dL, 0x43c03f9bf001200cL,
            0x68afbc40524325d3L, 0x77f04904fa4355d1L, 0x5c9fcadf5801500eL, 0x212f4eb3bec75e6fL, 0xa40cd681c855bb0L,
            0x1f90a43aeec7be6bL, 0x34ff27e14c85bbb4L, 0x494fa38daa43b5d5L, 0x622020560801b00aL, 0x7d7fd512a001c008L,
            0x561056c90243c5d7L, 0x2ba0d2a5e485cbb6L, 0xcf517e46c7ce69L, 0x2a3e7059690a5764L, 0x151f382cb4852bbL,
            0x7ce177ee2d8e5cdaL, 0x578ef4358fcc5905L, 0x48d1017127cc2907L, 0x63be82aa858e2cd8L, 0x1e0e06c6634822b9L,
            0x3561851dc10a2766L, 0x20b1ec4f3348c2bdL, 0xbde6f94910ac762L, 0x766eebf877ccc903L, 0x5d016823d58eccdcL,
            0x425e9d677d8ebcdeL, 0x69311ebcdfccb901L, 0x14819ad0390ab760L, 0x3fee190b9b48b2bfL, 0x3f214875dd8f7cd6L,
            0x144ecbae7fcd7909L, 0x69fe4fc2990b7768L, 0x4291cc193b4972b7L, 0x5dce395d934902b5L, 0x76a1ba86310b076aL,
            0xb113eead7cd090bL, 0x207ebd31758f0cd4L, 0x35aed46387cde90fL, 0x1ec157b8258fecd0L, 0x6371d3d4c349e2b1L,
            0x481e500f610be76eL, 0x5741a54bc90b976cL, 0x7c2e26906b4992b3L, 0x19ea2fc8d8f9cd2L, 0x2af121272fcd990dL,
            0x547ce0b2d214aec8L, 0x7f1363697056ab17L, 0x2a3e7059690a576L, 0x29cc64de34d2a0a9L, 0x3693919a9cd2d0abL,
            0x1dfc12413e90d574L, 0x604c962dd856db15L, 0x4b2315f67a14decaL, 0x5ef37ca488563b11L, 0x759cff7f2a143eceL,
            0x82c7b13ccd230afL, 0x2343f8c86e903570L, 0x3c1c0d8cc6904572L, 0x17738e5764d240adL, 0x6ac30a3b82144eccL,
            0x41ac89e020564b13L, 0x4163d89e6691857aL, 0x6a0c5b45c4d380a5L, 0x17bcdf2922158ec4L, 0x3cd35cf280578b1bL,
            0x238ca9b62857fb19L, 0x8e32a6d8a15fec6L, 0x7553ae016cd3f0a7L, 0x5e3c2ddace91f578L, 0x4bec44883cd310a3L,
            0x6083c7539e91157cL, 0x1d33433f78571b1dL, 0x365cc0e4da151ec2L, 0x290335a072156ec0L, 0x26cb67bd0576b1fL,
            0x7fdc32173691657eL, 0x54b3b1cc94d360a1L, 0x7e4290ebbb1ef9acL, 0x552d1330195cfc73L, 0x289d975cff9af212L,
            0x3f214875dd8f7cdL, 0x1cade1c3f5d887cfL, 0x37c26218579a8210L, 0x4a72e674b15c8c71L, 0x611d65af131e89aeL,
            0x74cd0cfde15c6c75L, 0x5fa28f26431e69aaL, 0x22120b4aa5d867cbL, 0x97d8891079a6214L, 0x16227dd5af9a1216L,
            0x3d4dfe0e0dd817c9L, 0x40fd7a62eb1e19a8L, 0x6b92f9b9495c1c77L, 0x6b5da8c70f9bd21eL, 0x40322b1cadd9d7c1L,
            0x3d82af704b1fd9a0L, 0x16ed2cabe95ddc7fL, 0x9b2d9ef415dac7dL, 0x22dd5a34e31fa9a2L, 0x5f6dde5805d9a7c3L,
            0x74025d83a79ba21cL, 0x61d234d155d947c7L, 0x4abdb70af79b4218L, 0x370d3366115d4c79L, 0x1c62b0bdb31f49a6L,
            0x33d45f91b1f39a4L, 0x2852c622b95d3c7bL, 0x55e2424e5f9b321aL, 0x7e8dc195fdd937c5L, 0x67a8bf2363e7348fL,
            0x4cc73cf8c1a53150L, 0x3177b89427633f31L, 0x1a183b4f85213aeeL, 0x547ce0b2d214aecL, 0x2e284dd08f634f33L,
            0x5398c9bc69a54152L, 0x78f74a67cbe7448dL, 0x6d27233539a5a156L, 0x4648a0ee9be7a489L, 0x3bf824827d21aae8L,
            0x1097a759df63af37L, 0xfc8521d7763df35L, 0x24a7d1c6d521daeaL, 0x591755aa33e7d48bL, 0x7278d67191a5d154L,
            0x72b7870fd7621f3dL, 0x59d804d475201ae2L, 0x246880b893e61483L, 0xf07036331a4115cL, 0x1058f62799a4615eL,
            0x3b3775fc3be66481L, 0x4687f190dd206ae0L, 0x6de8724b7f626f3fL, 0x78381b198d208ae4L, 0x535798c22f628f3bL,
            0x2ee71caec9a4815aL, 0x5889f756be68485L, 0x1ad76a31c3e6f487L, 0x31b8e9ea61a4f158L, 0x4c086d868762ff39L,
            0x6767ee5d2520fae6L, 0x4d96cf7a0aed63ebL, 0x66f94ca1a8af6634L, 0x1b49c8cd4e696855L, 0x30264b16ec2b6d8aL,
            0x2f79be52442b1d88L, 0x4163d89e6691857L, 0x79a6b9e500af1636L, 0x52c93a3ea2ed13e9L, 0x4719536c50aff632L,
            0x6c76d0b7f2edf3edL, 0x11c654db142bfd8cL, 0x3aa9d700b669f853L, 0x25f622441e698851L, 0xe99a19fbc2b8d8eL,
            0x732925f35aed83efL, 0x5846a628f8af8630L, 0x5889f756be684859L, 0x73e6748d1c2a4d86L, 0xe56f0e1faec43e7L,
            0x2539733a58ae4638L, 0x3a66867ef0ae363aL, 0x110905a552ec33e5L, 0x6cb981c9b42a3d84L, 0x47d602121668385bL,
            0x52066b40e42add80L, 0x7969e89b4668d85fL, 0x4d96cf7a0aed63eL, 0x2fb6ef2c02ecd3e1L, 0x30e91a68aaeca3e3L,
            0x1b8699b308aea63cL, 0x66361ddfee68a85dL, 0x4d599e044c2aad82L, 0x33d45f91b1f39a47L, 0x18bbdc4a13b19f98L,
            0x650b5826f57791f9L, 0x4e64dbfd57359426L, 0x513b2eb9ff35e424L, 0x7a54ad625d77e1fbL, 0x7e4290ebbb1ef9aL,
            0x2c8baad519f3ea45L, 0x395bc387ebb10f9eL, 0x1234405c49f30a41L, 0x6f84c430af350420L, 0x44eb47eb0d7701ffL,
            0x5bb4b2afa57771fdL, 0x70db317407357422L, 0xd6bb518e1f37a43L, 0x260436c343b17f9cL, 0x26cb67bd0576b1f5L,
            0xda4e466a734b42aL, 0x7014600a41f2ba4bL, 0x5b7be3d1e3b0bf94L, 0x442416954bb0cf96L, 0x6f4b954ee9f2ca49L,
            0x12fb11220f34c428L, 0x399492f9ad76c1f7L, 0x2c44fbab5f34242cL, 0x72b7870fd7621f3L, 0x7a9bfc1c1bb02f92L,
            0x51f47fc7b9f22a4dL, 0x4eab8a8311f25a4fL, 0x65c40958b3b05f90L, 0x18748d34557651f1L, 0x331b0eeff734542eL,
            0x19ea2fc8d8f9cd23L, 0x3285ac137abbc8fcL, 0x4f35287f9c7dc69dL, 0x645aaba43e3fc342L, 0x7b055ee0963fb340L,
            0x506add3b347db69fL, 0x2dda5957d2bbb8feL, 0x6b5da8c70f9bd21L, 0x1365b3de82bb58faL, 0x380a300520f95d25L,
            0x45bab469c63f5344L, 0x6ed537b2647d569bL, 0x718ac2f6cc7d2699L, 0x5ae5412d6e3f2346L, 0x2755c54188f92d27L,
            0xc3a469a2abb28f8L, 0xcf517e46c7ce691L, 0x279a943fce3ee34eL, 0x5a2a105328f8ed2fL, 0x714593888abae8f0L,
            0x6e1a66cc22ba98f2L, 0x4575e51780f89d2dL, 0x38c5617b663e934cL, 0x13aae2a0c47c9693L, 0x67a8bf2363e7348L,
            0x2d150829947c7697L, 0x50a58c4572ba78f6L, 0x7bca0f9ed0f87d29L, 0x6495fada78f80d2bL, 0x4ffa7901daba08f4L,
            0x324afd6d3c7c0695L, 0x19257eb69e3e034aL };

    /**
     * 将字符串转换为long类型
     *
     * @param inputStr 需要转换的字符串
     * @return long
     */
    public static long newFP63(String inputStr) {
        byte[] byteArray = inputStr.getBytes();
        return newFP63(byteArray, 0, byteArray.length);
    }

    /**
     * 将字节数组转换为long类型，转换范围为[pos, pos+length)
     *
     * @param byteArray 需要转换的字节数组
     * @param pos 起始下标
     * @param length 转换字节数
     * @return long
     */
    public static long newFP63(byte[] byteArray, int pos, int length) {
        return extendFP63(IRRED_POLY, byteArray, pos, length) << 1;
    }

    /**
     * 字节数组转换为long
     *
     * @param fp 指纹种子
     * @param byteArray 需要转换的字节数组
     * @param pos 起始下标
     * @param length 转换字节数
     * @return long
     */
    private static long extendFP63(long fp, byte[] byteArray, int pos, int length) {
        int mask = 0xFF;
        int end = pos + length;
        for (int i = pos; i < end; i++) {
            fp = (fp >> 8) ^ byteModeTable7[(byteArray[i] ^ (int)fp) & mask];
        }
        if ((fp & X63) != 0) {
            fp ^= 0xCF517E46C7CE691FL;
        }
        return fp;
    }

}


