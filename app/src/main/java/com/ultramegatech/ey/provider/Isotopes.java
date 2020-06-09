/*
 * The MIT License (MIT)
 * Copyright © 2012 Steve Guidetti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ultramegatech.ey.provider;

import androidx.annotation.Nullable;

/**
 * Contains data on the common isotopes.
 *
 * @author Steve Guidetti
 */
public class Isotopes {
    /**
     * The list of isotopes grouped by element
     */
    private static final Isotope[][] ISOTOPES = new Isotope[][] {
            new Isotope[] {
                    new Isotope(1, "H", 1.00782503, 0.999885),
                    new Isotope(2, "D", 2.01410178, 0.000115),
                    new Isotope(3, "T", 3.01604928, null)
            },
            new Isotope[] {
                    new Isotope(3, "He", 3.01602932, 0.00000134),
                    new Isotope(4, "He", 4.00260325, 0.99999866)
            },
            new Isotope[] {
                    new Isotope(6, "Li", 6.01512289, 0.0759),
                    new Isotope(7, "Li", 7.01600344, 0.9241)
            },
            new Isotope[] {
                    new Isotope(9, "Be", 9.01218306, 1.0)
            },
            new Isotope[] {
                    new Isotope(10, "B", 10.01293695, 0.199),
                    new Isotope(11, "B", 11.00930536, 0.801)
            },
            new Isotope[] {
                    new Isotope(12, "C", 12.0, 0.9893),
                    new Isotope(13, "C", 13.00335484, 0.0107),
                    new Isotope(14, "C", 14.00324199, null)
            },
            new Isotope[] {
                    new Isotope(14, "N", 14.003074, 0.99636),
                    new Isotope(15, "N", 15.0001089, 0.00364)
            },
            new Isotope[] {
                    new Isotope(16, "O", 15.99491462, 0.99757),
                    new Isotope(17, "O", 16.99913176, 0.00038),
                    new Isotope(18, "O", 17.99915961, 0.00205)
            },
            new Isotope[] {
                    new Isotope(19, "F", 18.99840316, 1.0)
            },
            new Isotope[] {
                    new Isotope(20, "Ne", 19.99244018, 0.9048),
                    new Isotope(21, "Ne", 20.99384668, 0.0027),
                    new Isotope(22, "Ne", 21.99138511, 0.0925)
            },
            new Isotope[] {
                    new Isotope(23, "Na", 22.98976928, 1.0)
            },
            new Isotope[] {
                    new Isotope(24, "Mg", 23.9850417, 0.7899),
                    new Isotope(25, "Mg", 24.98583698, 0.1),
                    new Isotope(26, "Mg", 25.98259297, 0.1101)
            },
            new Isotope[] {
                    new Isotope(27, "Al", 26.98153853, 1.0)
            },
            new Isotope[] {
                    new Isotope(28, "Si", 27.97692653, 0.92223),
                    new Isotope(29, "Si", 28.97649466, 0.04685),
                    new Isotope(30, "Si", 29.97377014, 0.03092)
            },
            new Isotope[] {
                    new Isotope(31, "P", 30.973762, 1.0)
            },
            new Isotope[] {
                    new Isotope(32, "S", 31.97207117, 0.9499),
                    new Isotope(33, "S", 32.97145891, 0.0075),
                    new Isotope(34, "S", 33.967867, 0.0425),
                    new Isotope(36, "S", 35.96708071, 0.0001)
            },
            new Isotope[] {
                    new Isotope(35, "Cl", 34.96885268, 0.7576),
                    new Isotope(37, "Cl", 36.9659026, 0.2424)
            },
            new Isotope[] {
                    new Isotope(36, "Ar", 35.9675451, 0.003336),
                    new Isotope(38, "Ar", 37.96273211, 0.000629),
                    new Isotope(40, "Ar", 39.96238312, 0.996035)
            },
            new Isotope[] {
                    new Isotope(39, "K", 38.96370649, 0.932581),
                    new Isotope(40, "K", 39.96399817, 0.000117),
                    new Isotope(41, "K", 40.96182526, 0.067302)
            },
            new Isotope[] {
                    new Isotope(40, "Ca", 39.96259086, 0.96941),
                    new Isotope(42, "Ca", 41.95861783, 0.00647),
                    new Isotope(43, "Ca", 42.95876644, 0.00135),
                    new Isotope(44, "Ca", 43.95548156, 0.02086),
                    new Isotope(46, "Ca", 45.953689, 0.00004),
                    new Isotope(48, "Ca", 47.95252276, 0.00187)
            },
            new Isotope[] {
                    new Isotope(45, "Sc", 44.95590828, 1.0)
            },
            new Isotope[] {
                    new Isotope(46, "Ti", 45.95262772, 0.0825),
                    new Isotope(47, "Ti", 46.95175879, 0.0744),
                    new Isotope(48, "Ti", 47.94794198, 0.7372),
                    new Isotope(49, "Ti", 48.94786568, 0.0541),
                    new Isotope(50, "Ti", 49.94478689, 0.0518)
            },
            new Isotope[] {
                    new Isotope(50, "V", 49.94715601, 0.0025),
                    new Isotope(51, "V", 50.94395704, 0.9975)
            },
            new Isotope[] {
                    new Isotope(50, "Cr", 49.94604183, 0.04345),
                    new Isotope(52, "Cr", 51.94050623, 0.83789),
                    new Isotope(53, "Cr", 52.94064815, 0.09501),
                    new Isotope(54, "Cr", 53.93887916, 0.02365)
            },
            new Isotope[] {
                    new Isotope(55, "Mn", 54.93804391, 1.0)
            },
            new Isotope[] {
                    new Isotope(54, "Fe", 53.93960899, 0.05845),
                    new Isotope(56, "Fe", 55.93493633, 0.91754),
                    new Isotope(57, "Fe", 56.93539284, 0.02119),
                    new Isotope(58, "Fe", 57.93327443, 0.00282)
            },
            new Isotope[] {
                    new Isotope(59, "Co", 58.93319429, 1.0)
            },
            new Isotope[] {
                    new Isotope(58, "Ni", 57.93534241, 0.68077),
                    new Isotope(60, "Ni", 59.93078588, 0.26223),
                    new Isotope(61, "Ni", 60.93105557, 0.011399),
                    new Isotope(62, "Ni", 61.92834537, 0.036346),
                    new Isotope(64, "Ni", 63.92796682, 0.009255)
            },
            new Isotope[] {
                    new Isotope(63, "Cu", 62.92959772, 0.6915),
                    new Isotope(65, "Cu", 64.9277897, 0.3085)
            },
            new Isotope[] {
                    new Isotope(64, "Zn", 63.92914201, 0.4917),
                    new Isotope(66, "Zn", 65.92603381, 0.2773),
                    new Isotope(67, "Zn", 66.92712775, 0.0404),
                    new Isotope(68, "Zn", 67.92484455, 0.1845),
                    new Isotope(70, "Zn", 69.9253192, 0.0061)
            },
            new Isotope[] {
                    new Isotope(69, "Ga", 68.9255735, 0.60108),
                    new Isotope(71, "Ga", 70.92470258, 0.39892)
            },
            new Isotope[] {
                    new Isotope(70, "Ge", 69.92424875, 0.2057),
                    new Isotope(72, "Ge", 71.92207583, 0.2745),
                    new Isotope(73, "Ge", 72.92345896, 0.0775),
                    new Isotope(74, "Ge", 73.92117776, 0.365),
                    new Isotope(76, "Ge", 75.92140273, 0.0773)
            },
            new Isotope[] {
                    new Isotope(75, "As", 74.92159457, 1.0)
            },
            new Isotope[] {
                    new Isotope(74, "Se", 73.92247593, 0.0089),
                    new Isotope(76, "Se", 75.9192137, 0.0937),
                    new Isotope(77, "Se", 76.91991415, 0.0763),
                    new Isotope(78, "Se", 77.91730928, 0.2377),
                    new Isotope(80, "Se", 79.9165218, 0.4961),
                    new Isotope(82, "Se", 81.9166995, 0.0873)
            },
            new Isotope[] {
                    new Isotope(79, "Br", 78.9183376, 0.5069),
                    new Isotope(81, "Br", 80.9162897, 0.4931)
            },
            new Isotope[] {
                    new Isotope(78, "Kr", 77.92036494, 0.00355),
                    new Isotope(80, "Kr", 79.91637808, 0.02286),
                    new Isotope(82, "Kr", 81.91348273, 0.11593),
                    new Isotope(83, "Kr", 82.91412716, 0.115),
                    new Isotope(84, "Kr", 83.91149773, 0.56987),
                    new Isotope(86, "Kr", 85.91061063, 0.17279)
            },
            new Isotope[] {
                    new Isotope(85, "Rb", 84.91178974, 0.7217),
                    new Isotope(87, "Rb", 86.90918053, 0.2783)
            },
            new Isotope[] {
                    new Isotope(84, "Sr", 83.9134191, 0.0056),
                    new Isotope(86, "Sr", 85.9092606, 0.0986),
                    new Isotope(87, "Sr", 86.9088775, 0.07),
                    new Isotope(88, "Sr", 87.9056125, 0.8258)
            },
            new Isotope[] {
                    new Isotope(89, "Y", 88.9058403, 1.0)
            },
            new Isotope[] {
                    new Isotope(90, "Zr", 89.9046977, 0.5145),
                    new Isotope(91, "Zr", 90.9056396, 0.1122),
                    new Isotope(92, "Zr", 91.9050347, 0.1715),
                    new Isotope(94, "Zr", 93.9063108, 0.1738),
                    new Isotope(96, "Zr", 95.9082714, 0.028)
            },
            new Isotope[] {
                    new Isotope(93, "Nb", 92.906373, 1.0)
            },
            new Isotope[] {
                    new Isotope(92, "Mo", 91.90680796, 0.1453),
                    new Isotope(94, "Mo", 93.9050849, 0.0915),
                    new Isotope(95, "Mo", 94.90583877, 0.1584),
                    new Isotope(96, "Mo", 95.90467612, 0.1667),
                    new Isotope(97, "Mo", 96.90601812, 0.096),
                    new Isotope(98, "Mo", 97.90540482, 0.2439),
                    new Isotope(100, "Mo", 99.9074718, 0.0982)
            },
            new Isotope[] {
                    new Isotope(97, "Tc", 96.9063667, null),
                    new Isotope(98, "Tc", 97.9072124, null),
                    new Isotope(99, "Tc", 98.9062508, null)
            },
            new Isotope[] {
                    new Isotope(96, "Ru", 95.90759025, 0.0554),
                    new Isotope(98, "Ru", 97.9052868, 0.0187),
                    new Isotope(99, "Ru", 98.9059341, 0.1276),
                    new Isotope(100, "Ru", 99.9042143, 0.126),
                    new Isotope(101, "Ru", 100.9055769, 0.1706),
                    new Isotope(102, "Ru", 101.9043441, 0.3155),
                    new Isotope(104, "Ru", 103.9054275, 0.1862)
            },
            new Isotope[] {
                    new Isotope(103, "Rh", 102.905498, 1.0)
            },
            new Isotope[] {
                    new Isotope(102, "Pd", 101.9056022, 0.0102),
                    new Isotope(104, "Pd", 103.9040305, 0.1114),
                    new Isotope(105, "Pd", 104.9050796, 0.2233),
                    new Isotope(106, "Pd", 105.9034804, 0.2733),
                    new Isotope(108, "Pd", 107.9038916, 0.2646),
                    new Isotope(110, "Pd", 109.9051722, 0.1172)
            },
            new Isotope[] {
                    new Isotope(107, "Ag", 106.9050916, 0.51839),
                    new Isotope(109, "Ag", 108.9047553, 0.48161)
            },
            new Isotope[] {
                    new Isotope(106, "Cd", 105.9064599, 0.0125),
                    new Isotope(108, "Cd", 107.9041834, 0.0089),
                    new Isotope(110, "Cd", 109.90300661, 0.1249),
                    new Isotope(111, "Cd", 110.90418287, 0.128),
                    new Isotope(112, "Cd", 111.90276287, 0.2413),
                    new Isotope(113, "Cd", 112.90440813, 0.1222),
                    new Isotope(114, "Cd", 113.90336509, 0.2873),
                    new Isotope(116, "Cd", 115.90476315, 0.0749)
            },
            new Isotope[] {
                    new Isotope(113, "In", 112.90406184, 0.0429),
                    new Isotope(115, "In", 114.90387878, 0.9571)
            },
            new Isotope[] {
                    new Isotope(112, "Sn", 111.90482387, 0.0097),
                    new Isotope(114, "Sn", 113.9027827, 0.0066),
                    new Isotope(115, "Sn", 114.9033447, 0.0034),
                    new Isotope(116, "Sn", 115.9017428, 0.1454),
                    new Isotope(117, "Sn", 116.90295398, 0.0768),
                    new Isotope(118, "Sn", 117.90160657, 0.2422),
                    new Isotope(119, "Sn", 118.90331117, 0.0859),
                    new Isotope(120, "Sn", 119.90220163, 0.3258),
                    new Isotope(122, "Sn", 121.9034438, 0.0463),
                    new Isotope(124, "Sn", 123.9052766, 0.0579)
            },
            new Isotope[] {
                    new Isotope(121, "Sb", 120.903812, 0.5721),
                    new Isotope(123, "Sb", 122.9042132, 0.4279)
            },
            new Isotope[] {
                    new Isotope(120, "Te", 119.9040593, 0.0009),
                    new Isotope(122, "Te", 121.9030435, 0.0255),
                    new Isotope(123, "Te", 122.9042698, 0.0089),
                    new Isotope(124, "Te", 123.9028171, 0.0474),
                    new Isotope(125, "Te", 124.9044299, 0.0707),
                    new Isotope(126, "Te", 125.9033109, 0.1884),
                    new Isotope(128, "Te", 127.90446128, 0.3174),
                    new Isotope(130, "Te", 129.90622275, 0.3408)
            },
            new Isotope[] {
                    new Isotope(127, "I", 126.9044719, 1.0)
            },
            new Isotope[] {
                    new Isotope(124, "Xe", 123.905892, 0.000952),
                    new Isotope(126, "Xe", 125.9042983, 0.00089),
                    new Isotope(128, "Xe", 127.903531, 0.019102),
                    new Isotope(129, "Xe", 128.90478086, 0.264006),
                    new Isotope(130, "Xe", 129.90350935, 0.04071),
                    new Isotope(131, "Xe", 130.90508406, 0.212324),
                    new Isotope(132, "Xe", 131.90415509, 0.269086),
                    new Isotope(134, "Xe", 133.90539466, 0.104357),
                    new Isotope(136, "Xe", 135.90721448, 0.088573)
            },
            new Isotope[] {
                    new Isotope(133, "Cs", 132.90545196, 1.0)
            },
            new Isotope[] {
                    new Isotope(130, "Ba", 129.9063207, 0.00106),
                    new Isotope(132, "Ba", 131.9050611, 0.00101),
                    new Isotope(134, "Ba", 133.90450818, 0.02417),
                    new Isotope(135, "Ba", 134.90568838, 0.06592),
                    new Isotope(136, "Ba", 135.90457573, 0.07854),
                    new Isotope(137, "Ba", 136.90582714, 0.11232),
                    new Isotope(138, "Ba", 137.905247, 0.71698)
            },
            new Isotope[] {
                    new Isotope(138, "La", 137.9071149, 0.0008881),
                    new Isotope(139, "La", 138.9063563, 0.9991119)
            },
            new Isotope[] {
                    new Isotope(136, "Ce", 135.90712921, 0.00185),
                    new Isotope(138, "Ce", 137.905991, 0.00251),
                    new Isotope(140, "Ce", 139.9054431, 0.8845),
                    new Isotope(142, "Ce", 141.9092504, 0.11114)
            },
            new Isotope[] {
                    new Isotope(141, "Pr", 140.9076576, 1.0)
            },
            new Isotope[] {
                    new Isotope(142, "Nd", 141.907729, 0.27152),
                    new Isotope(143, "Nd", 142.90982, 0.12174),
                    new Isotope(144, "Nd", 143.910093, 0.23798),
                    new Isotope(145, "Nd", 144.9125793, 0.08293),
                    new Isotope(146, "Nd", 145.9131226, 0.17189),
                    new Isotope(148, "Nd", 147.9168993, 0.05756),
                    new Isotope(150, "Nd", 149.9209022, 0.05638)
            },
            new Isotope[] {
                    new Isotope(145, "Pm", 144.9127559, null),
                    new Isotope(147, "Pm", 146.915145, null)
            },
            new Isotope[] {
                    new Isotope(144, "Sm", 143.9120065, 0.0307),
                    new Isotope(147, "Sm", 146.9149044, 0.1499),
                    new Isotope(148, "Sm", 147.9148292, 0.1124),
                    new Isotope(149, "Sm", 148.9171921, 0.1382),
                    new Isotope(150, "Sm", 149.9172829, 0.0738),
                    new Isotope(152, "Sm", 151.9197397, 0.2675),
                    new Isotope(154, "Sm", 153.9222169, 0.2275)
            },
            new Isotope[] {
                    new Isotope(151, "Eu", 150.9198578, 0.4781),
                    new Isotope(153, "Eu", 152.921238, 0.5219)
            },
            new Isotope[] {
                    new Isotope(152, "Gd", 151.9197995, 0.002),
                    new Isotope(154, "Gd", 153.9208741, 0.0218),
                    new Isotope(155, "Gd", 154.9226305, 0.148),
                    new Isotope(156, "Gd", 155.9221312, 0.2047),
                    new Isotope(157, "Gd", 156.9239686, 0.1565),
                    new Isotope(158, "Gd", 157.9241123, 0.2484),
                    new Isotope(160, "Gd", 159.9270624, 0.2186)
            },
            new Isotope[] {
                    new Isotope(159, "Tb", 158.9253547, 1.0)
            },
            new Isotope[] {
                    new Isotope(156, "Dy", 155.9242847, 0.00056),
                    new Isotope(158, "Dy", 157.9244159, 0.00095),
                    new Isotope(160, "Dy", 159.9252046, 0.02329),
                    new Isotope(161, "Dy", 160.9269405, 0.18889),
                    new Isotope(162, "Dy", 161.9268056, 0.25475),
                    new Isotope(163, "Dy", 162.9287383, 0.24896),
                    new Isotope(164, "Dy", 163.9291819, 0.2826)
            },
            new Isotope[] {
                    new Isotope(165, "Ho", 164.9303288, 1.0)
            },
            new Isotope[] {
                    new Isotope(162, "Er", 161.9287884, 0.00139),
                    new Isotope(164, "Er", 163.9292088, 0.01601),
                    new Isotope(166, "Er", 165.9302995, 0.33503),
                    new Isotope(167, "Er", 166.9320546, 0.22869),
                    new Isotope(168, "Er", 167.9323767, 0.26978),
                    new Isotope(170, "Er", 169.9354702, 0.1491)
            },
            new Isotope[] {
                    new Isotope(169, "Tm", 168.9342179, 1.0)
            },
            new Isotope[] {
                    new Isotope(168, "Yb", 167.9338896, 0.00123),
                    new Isotope(170, "Yb", 169.9347664, 0.02982),
                    new Isotope(171, "Yb", 170.9363302, 0.1409),
                    new Isotope(172, "Yb", 171.9363859, 0.2168),
                    new Isotope(173, "Yb", 172.9382151, 0.16103),
                    new Isotope(174, "Yb", 173.9388664, 0.32026),
                    new Isotope(176, "Yb", 175.9425764, 0.12996)
            },
            new Isotope[] {
                    new Isotope(175, "Lu", 174.9407752, 0.97401),
                    new Isotope(176, "Lu", 175.9426897, 0.02599)
            },
            new Isotope[] {
                    new Isotope(174, "Hf", 173.9400461, 0.0016),
                    new Isotope(176, "Hf", 175.9414076, 0.0526),
                    new Isotope(177, "Hf", 176.9432277, 0.186),
                    new Isotope(178, "Hf", 177.9437058, 0.2728),
                    new Isotope(179, "Hf", 178.9458232, 0.1362),
                    new Isotope(180, "Hf", 179.946557, 0.3508)
            },
            new Isotope[] {
                    new Isotope(180, "Ta", 179.9474648, 0.0001201),
                    new Isotope(181, "Ta", 180.9479958, 0.9998799)
            },
            new Isotope[] {
                    new Isotope(180, "W", 179.9467108, 0.0012),
                    new Isotope(182, "W", 181.94820394, 0.265),
                    new Isotope(183, "W", 182.95022275, 0.1431),
                    new Isotope(184, "W", 183.95093092, 0.3064),
                    new Isotope(186, "W", 185.9543628, 0.2843)
            },
            new Isotope[] {
                    new Isotope(185, "Re", 184.9529545, 0.374),
                    new Isotope(187, "Re", 186.9557501, 0.626)
            },
            new Isotope[] {
                    new Isotope(184, "Os", 183.9524885, 0.0002),
                    new Isotope(186, "Os", 185.953835, 0.0159),
                    new Isotope(187, "Os", 186.9557474, 0.0196),
                    new Isotope(188, "Os", 187.9558352, 0.1324),
                    new Isotope(189, "Os", 188.9581442, 0.1615),
                    new Isotope(190, "Os", 189.9584437, 0.2626),
                    new Isotope(192, "Os", 191.961477, 0.4078)
            },
            new Isotope[] {
                    new Isotope(191, "Ir", 190.9605893, 0.373),
                    new Isotope(193, "Ir", 192.9629216, 0.627)
            },
            new Isotope[] {
                    new Isotope(190, "Pt", 189.9599297, 0.00012),
                    new Isotope(192, "Pt", 191.9610387, 0.00782),
                    new Isotope(194, "Pt", 193.9626809, 0.3286),
                    new Isotope(195, "Pt", 194.9647917, 0.3378),
                    new Isotope(196, "Pt", 195.96495209, 0.2521),
                    new Isotope(198, "Pt", 197.9678949, 0.07356)
            },
            new Isotope[] {
                    new Isotope(197, "Au", 196.96656879, 1.0)
            },
            new Isotope[] {
                    new Isotope(196, "Hg", 195.9658326, 0.0015),
                    new Isotope(198, "Hg", 197.9667686, 0.0997),
                    new Isotope(199, "Hg", 198.96828064, 0.1687),
                    new Isotope(200, "Hg", 199.96832659, 0.231),
                    new Isotope(201, "Hg", 200.97030284, 0.1318),
                    new Isotope(202, "Hg", 201.9706434, 0.2986),
                    new Isotope(204, "Hg", 203.97349398, 0.0687)
            },
            new Isotope[] {
                    new Isotope(203, "Tl", 202.9723446, 0.2952),
                    new Isotope(205, "Tl", 204.9744278, 0.7048)
            },
            new Isotope[] {
                    new Isotope(204, "Pb", 203.973044, 0.014),
                    new Isotope(206, "Pb", 205.9744657, 0.241),
                    new Isotope(207, "Pb", 206.9758973, 0.221),
                    new Isotope(208, "Pb", 207.9766525, 0.524)
            },
            new Isotope[] {
                    new Isotope(209, "Bi", 208.9803991, 1.0)
            },
            new Isotope[] {
                    new Isotope(209, "Po", 208.9824308, null),
                    new Isotope(210, "Po", 209.9828741, null)
            },
            new Isotope[] {
                    new Isotope(210, "At", 209.9871479, null),
                    new Isotope(211, "At", 210.9874966, null)
            },
            new Isotope[] {
                    new Isotope(211, "Rn", 210.9906011, null),
                    new Isotope(220, "Rn", 220.0113941, null),
                    new Isotope(222, "Rn", 222.0175782, null)
            },
            new Isotope[] {
                    new Isotope(223, "Fr", 223.019736, null)
            },
            new Isotope[] {
                    new Isotope(223, "Ra", 223.0185023, null),
                    new Isotope(224, "Ra", 224.020212, null),
                    new Isotope(226, "Ra", 226.0254103, null),
                    new Isotope(228, "Ra", 228.0310707, null)
            },
            new Isotope[] {
                    new Isotope(227, "Ac", 227.0277523, null)
            },
            new Isotope[] {
                    new Isotope(230, "Th", 230.0331341, null),
                    new Isotope(232, "Th", 232.0380558, 1.0)
            },
            new Isotope[] {
                    new Isotope(231, "Pa", 231.0358842, 1.0)
            },
            new Isotope[] {
                    new Isotope(233, "U", 233.0396355, null),
                    new Isotope(234, "U", 234.0409523, 0.000054),
                    new Isotope(235, "U", 235.0439301, 0.007204),
                    new Isotope(236, "U", 236.0455682, null),
                    new Isotope(238, "U", 238.0507884, 0.992742)
            },
            new Isotope[] {
                    new Isotope(236, "Np", 236.04657, null),
                    new Isotope(237, "Np", 237.0481736, null)
            },
            new Isotope[] {
                    new Isotope(238, "Pu", 238.0495601, null),
                    new Isotope(239, "Pu", 239.0521636, null),
                    new Isotope(240, "Pu", 240.0538138, null),
                    new Isotope(241, "Pu", 241.0568517, null),
                    new Isotope(242, "Pu", 242.0587428, null),
                    new Isotope(244, "Pu", 244.0642053, null)
            },
            new Isotope[] {
                    new Isotope(241, "Am", 241.0568293, null),
                    new Isotope(243, "Am", 243.0613813, null)
            },
            new Isotope[] {
                    new Isotope(243, "Cm", 243.0613893, null),
                    new Isotope(244, "Cm", 244.0627528, null),
                    new Isotope(245, "Cm", 245.0654915, null),
                    new Isotope(246, "Cm", 246.0672238, null),
                    new Isotope(247, "Cm", 247.0703541, null),
                    new Isotope(248, "Cm", 248.0723499, null)
            },
            new Isotope[] {
                    new Isotope(247, "Bk", 247.0703073, null),
                    new Isotope(249, "Bk", 249.0749877, null)
            },
            new Isotope[] {
                    new Isotope(249, "Cf", 249.0748539, null),
                    new Isotope(250, "Cf", 250.0764062, null),
                    new Isotope(251, "Cf", 251.0795886, null),
                    new Isotope(252, "Cf", 252.0816272, null)
            },
            new Isotope[] {
                    new Isotope(252, "Es", 252.08298, null)
            },
            new Isotope[] {
                    new Isotope(257, "Fm", 257.0951061, null)
            },
            new Isotope[] {
                    new Isotope(258, "Md", 258.0984315, null),
                    new Isotope(260, "Md", 260.10365, null)
            },
            new Isotope[] {
                    new Isotope(259, "No", 259.10103, null)
            },
            new Isotope[] {
                    new Isotope(262, "Lr", 262.10961, null)
            },
            new Isotope[] {
                    new Isotope(267, "Rf", 267.12179, null)
            },
            new Isotope[] {
                    new Isotope(268, "Db", 268.12567, null)
            },
            new Isotope[] {
                    new Isotope(271, "Sg", 271.13393, null)
            },
            new Isotope[] {
                    new Isotope(272, "Bh", 272.13826, null)
            },
            new Isotope[] {
                    new Isotope(270, "Hs", 270.13429, null)
            },
            new Isotope[] {
                    new Isotope(276, "Mt", 276.15159, null)
            },
            new Isotope[] {
                    new Isotope(281, "Ds", 281.16451, null)
            },
            new Isotope[] {
                    new Isotope(280, "Rg", 280.16514, null)
            },
            new Isotope[] {
                    new Isotope(285, "Cn", 285.17712, null)
            },
            new Isotope[] {
                    new Isotope(284, "Nh", 284.17873, null)
            },
            new Isotope[] {
                    new Isotope(289, "Fl", 289.19042, null)
            },
            new Isotope[] {
                    new Isotope(288, "Mc", 288.19274, null)
            },
            new Isotope[] {
                    new Isotope(293, "Lv", 293.20449, null)
            },
            new Isotope[] {
                    new Isotope(292, "Ts", 292.20746, null)
            },
            new Isotope[] {
                    new Isotope(294, "Og", 294.21392, null)
            }
    };

    /**
     * Get the list of isotopes for an element.
     *
     * @param number The atomic number of the element
     * @return The list of isotopes for the element
     */
    @Nullable
    public static Isotope[] getIsotopes(int number) {
        if(number < 1 || number > ISOTOPES.length) {
            return null;
        }
        return ISOTOPES[number - 1].clone();
    }
}
