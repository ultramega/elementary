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

import android.support.annotation.Nullable;

/**
 * Contains data on the common isotopes.
 *
 * @author Steve Guidetti
 */
public class Isotopes {
    /**
     * The list of isotopes grouper by element
     */
    private static final Isotope[][] ISOTOPES = new Isotope[][] {
            new Isotope[] {
                    new Isotope(1, "H", 1.00782503),
                    new Isotope(2, "D", 2.01410178),
                    new Isotope(3, "T", 3.01604928)
            },
            new Isotope[] {
                    new Isotope(3, "He", 3.01602932),
                    new Isotope(4, "He", 4.00260325)
            },
            new Isotope[] {
                    new Isotope(6, "Li", 6.01512289),
                    new Isotope(7, "Li", 7.01600344)
            },
            new Isotope[] {
                    new Isotope(9, "Be", 9.01218306)
            },
            new Isotope[] {
                    new Isotope(10, "B", 10.01293695),
                    new Isotope(11, "B", 11.00930536)
            },
            new Isotope[] {
                    new Isotope(12, "C", 12.0),
                    new Isotope(13, "C", 13.00335484),
                    new Isotope(14, "C", 14.00324199)
            },
            new Isotope[] {
                    new Isotope(14, "N", 14.003074),
                    new Isotope(15, "N", 15.0001089)
            },
            new Isotope[] {
                    new Isotope(16, "O", 15.99491462),
                    new Isotope(17, "O", 16.99913176),
                    new Isotope(18, "O", 17.99915961)
            },
            new Isotope[] {
                    new Isotope(19, "F", 18.99840316)
            },
            new Isotope[] {
                    new Isotope(20, "Ne", 19.99244018),
                    new Isotope(21, "Ne", 20.99384668),
                    new Isotope(22, "Ne", 21.99138511)
            },
            new Isotope[] {
                    new Isotope(23, "Na", 22.98976928)
            },
            new Isotope[] {
                    new Isotope(24, "Mg", 23.9850417),
                    new Isotope(25, "Mg", 24.98583698),
                    new Isotope(26, "Mg", 25.98259297)
            },
            new Isotope[] {
                    new Isotope(27, "Al", 26.98153853)
            },
            new Isotope[] {
                    new Isotope(28, "Si", 27.97692653),
                    new Isotope(29, "Si", 28.97649466),
                    new Isotope(30, "Si", 29.97377014)
            },
            new Isotope[] {
                    new Isotope(31, "P", 30.973762)
            },
            new Isotope[] {
                    new Isotope(32, "S", 31.97207117),
                    new Isotope(33, "S", 32.97145891),
                    new Isotope(34, "S", 33.967867),
                    new Isotope(36, "S", 35.96708071)
            },
            new Isotope[] {
                    new Isotope(35, "Cl", 34.96885268),
                    new Isotope(37, "Cl", 36.9659026)
            },
            new Isotope[] {
                    new Isotope(36, "Ar", 35.9675451),
                    new Isotope(38, "Ar", 37.96273211),
                    new Isotope(40, "Ar", 39.96238312)
            },
            new Isotope[] {
                    new Isotope(39, "K", 38.96370649),
                    new Isotope(40, "K", 39.96399817),
                    new Isotope(41, "K", 40.96182526)
            },
            new Isotope[] {
                    new Isotope(40, "Ca", 39.96259086),
                    new Isotope(42, "Ca", 41.95861783),
                    new Isotope(43, "Ca", 42.95876644),
                    new Isotope(44, "Ca", 43.95548156),
                    new Isotope(46, "Ca", 45.953689),
                    new Isotope(48, "Ca", 47.95252276)
            },
            new Isotope[] {
                    new Isotope(45, "Sc", 44.95590828)
            },
            new Isotope[] {
                    new Isotope(46, "Ti", 45.95262772),
                    new Isotope(47, "Ti", 46.95175879),
                    new Isotope(48, "Ti", 47.94794198),
                    new Isotope(49, "Ti", 48.94786568),
                    new Isotope(50, "Ti", 49.94478689)
            },
            new Isotope[] {
                    new Isotope(50, "V", 49.94715601),
                    new Isotope(51, "V", 50.94395704)
            },
            new Isotope[] {
                    new Isotope(50, "Cr", 49.94604183),
                    new Isotope(52, "Cr", 51.94050623),
                    new Isotope(53, "Cr", 52.94064815),
                    new Isotope(54, "Cr", 53.93887916)
            },
            new Isotope[] {
                    new Isotope(55, "Mn", 54.93804391)
            },
            new Isotope[] {
                    new Isotope(54, "Fe", 53.93960899),
                    new Isotope(56, "Fe", 55.93493633),
                    new Isotope(57, "Fe", 56.93539284),
                    new Isotope(58, "Fe", 57.93327443)
            },
            new Isotope[] {
                    new Isotope(59, "Co", 58.93319429)
            },
            new Isotope[] {
                    new Isotope(58, "Ni", 57.93534241),
                    new Isotope(60, "Ni", 59.93078588),
                    new Isotope(61, "Ni", 60.93105557),
                    new Isotope(62, "Ni", 61.92834537),
                    new Isotope(64, "Ni", 63.92796682)
            },
            new Isotope[] {
                    new Isotope(63, "Cu", 62.92959772),
                    new Isotope(65, "Cu", 64.9277897)
            },
            new Isotope[] {
                    new Isotope(64, "Zn", 63.92914201),
                    new Isotope(66, "Zn", 65.92603381),
                    new Isotope(67, "Zn", 66.92712775),
                    new Isotope(68, "Zn", 67.92484455),
                    new Isotope(70, "Zn", 69.9253192)
            },
            new Isotope[] {
                    new Isotope(69, "Ga", 68.9255735),
                    new Isotope(71, "Ga", 70.92470258)
            },
            new Isotope[] {
                    new Isotope(70, "Ge", 69.92424875),
                    new Isotope(72, "Ge", 71.92207583),
                    new Isotope(73, "Ge", 72.92345896),
                    new Isotope(74, "Ge", 73.92117776),
                    new Isotope(76, "Ge", 75.92140273)
            },
            new Isotope[] {
                    new Isotope(75, "As", 74.92159457)
            },
            new Isotope[] {
                    new Isotope(74, "Se", 73.92247593),
                    new Isotope(76, "Se", 75.9192137),
                    new Isotope(77, "Se", 76.91991415),
                    new Isotope(78, "Se", 77.91730928),
                    new Isotope(80, "Se", 79.9165218),
                    new Isotope(82, "Se", 81.9166995)
            },
            new Isotope[] {
                    new Isotope(79, "Br", 78.9183376),
                    new Isotope(81, "Br", 80.9162897)
            },
            new Isotope[] {
                    new Isotope(78, "Kr", 77.92036494),
                    new Isotope(80, "Kr", 79.91637808),
                    new Isotope(82, "Kr", 81.91348273),
                    new Isotope(83, "Kr", 82.91412716),
                    new Isotope(84, "Kr", 83.91149773),
                    new Isotope(86, "Kr", 85.91061063)
            },
            new Isotope[] {
                    new Isotope(85, "Rb", 84.91178974),
                    new Isotope(87, "Rb", 86.90918053)
            },
            new Isotope[] {
                    new Isotope(84, "Sr", 83.9134191),
                    new Isotope(86, "Sr", 85.9092606),
                    new Isotope(87, "Sr", 86.9088775),
                    new Isotope(88, "Sr", 87.9056125)
            },
            new Isotope[] {
                    new Isotope(89, "Y", 88.9058403)
            },
            new Isotope[] {
                    new Isotope(90, "Zr", 89.9046977),
                    new Isotope(91, "Zr", 90.9056396),
                    new Isotope(92, "Zr", 91.9050347),
                    new Isotope(94, "Zr", 93.9063108),
                    new Isotope(96, "Zr", 95.9082714)
            },
            new Isotope[] {
                    new Isotope(93, "Nb", 92.906373)
            },
            new Isotope[] {
                    new Isotope(92, "Mo", 91.90680796),
                    new Isotope(94, "Mo", 93.9050849),
                    new Isotope(95, "Mo", 94.90583877),
                    new Isotope(96, "Mo", 95.90467612),
                    new Isotope(97, "Mo", 96.90601812),
                    new Isotope(98, "Mo", 97.90540482),
                    new Isotope(100, "Mo", 99.9074718)
            },
            new Isotope[] {
                    new Isotope(97, "Tc", 96.9063667),
                    new Isotope(98, "Tc", 97.9072124),
                    new Isotope(99, "Tc", 98.9062508)
            },
            new Isotope[] {
                    new Isotope(96, "Ru", 95.90759025),
                    new Isotope(98, "Ru", 97.9052868),
                    new Isotope(99, "Ru", 98.9059341),
                    new Isotope(100, "Ru", 99.9042143),
                    new Isotope(101, "Ru", 100.9055769),
                    new Isotope(102, "Ru", 101.9043441),
                    new Isotope(104, "Ru", 103.9054275)
            },
            new Isotope[] {
                    new Isotope(103, "Rh", 102.905498)
            },
            new Isotope[] {
                    new Isotope(102, "Pd", 101.9056022),
                    new Isotope(104, "Pd", 103.9040305),
                    new Isotope(105, "Pd", 104.9050796),
                    new Isotope(106, "Pd", 105.9034804),
                    new Isotope(108, "Pd", 107.9038916),
                    new Isotope(110, "Pd", 109.9051722)
            },
            new Isotope[] {
                    new Isotope(107, "Ag", 106.9050916),
                    new Isotope(109, "Ag", 108.9047553)
            },
            new Isotope[] {
                    new Isotope(106, "Cd", 105.9064599),
                    new Isotope(108, "Cd", 107.9041834),
                    new Isotope(110, "Cd", 109.90300661),
                    new Isotope(111, "Cd", 110.90418287),
                    new Isotope(112, "Cd", 111.90276287),
                    new Isotope(113, "Cd", 112.90440813),
                    new Isotope(114, "Cd", 113.90336509),
                    new Isotope(116, "Cd", 115.90476315)
            },
            new Isotope[] {
                    new Isotope(113, "In", 112.90406184),
                    new Isotope(115, "In", 114.90387878)
            },
            new Isotope[] {
                    new Isotope(112, "Sn", 111.90482387),
                    new Isotope(114, "Sn", 113.9027827),
                    new Isotope(115, "Sn", 114.9033447),
                    new Isotope(116, "Sn", 115.9017428),
                    new Isotope(117, "Sn", 116.90295398),
                    new Isotope(118, "Sn", 117.90160657),
                    new Isotope(119, "Sn", 118.90331117),
                    new Isotope(120, "Sn", 119.90220163),
                    new Isotope(122, "Sn", 121.9034438),
                    new Isotope(124, "Sn", 123.9052766)
            },
            new Isotope[] {
                    new Isotope(121, "Sb", 120.903812),
                    new Isotope(123, "Sb", 122.9042132)
            },
            new Isotope[] {
                    new Isotope(120, "Te", 119.9040593),
                    new Isotope(122, "Te", 121.9030435),
                    new Isotope(123, "Te", 122.9042698),
                    new Isotope(124, "Te", 123.9028171),
                    new Isotope(125, "Te", 124.9044299),
                    new Isotope(126, "Te", 125.9033109),
                    new Isotope(128, "Te", 127.90446128),
                    new Isotope(130, "Te", 129.90622275)
            },
            new Isotope[] {
                    new Isotope(127, "I", 126.9044719)
            },
            new Isotope[] {
                    new Isotope(124, "Xe", 123.905892),
                    new Isotope(126, "Xe", 125.9042983),
                    new Isotope(128, "Xe", 127.903531),
                    new Isotope(129, "Xe", 128.90478086),
                    new Isotope(130, "Xe", 129.90350935),
                    new Isotope(131, "Xe", 130.90508406),
                    new Isotope(132, "Xe", 131.90415509),
                    new Isotope(134, "Xe", 133.90539466),
                    new Isotope(136, "Xe", 135.90721448)
            },
            new Isotope[] {
                    new Isotope(133, "Cs", 132.90545196)
            },
            new Isotope[] {
                    new Isotope(130, "Ba", 129.9063207),
                    new Isotope(132, "Ba", 131.9050611),
                    new Isotope(134, "Ba", 133.90450818),
                    new Isotope(135, "Ba", 134.90568838),
                    new Isotope(136, "Ba", 135.90457573),
                    new Isotope(137, "Ba", 136.90582714),
                    new Isotope(138, "Ba", 137.905247)
            },
            new Isotope[] {
                    new Isotope(138, "La", 137.9071149),
                    new Isotope(139, "La", 138.9063563)
            },
            new Isotope[] {
                    new Isotope(136, "Ce", 135.90712921),
                    new Isotope(138, "Ce", 137.905991),
                    new Isotope(140, "Ce", 139.9054431),
                    new Isotope(142, "Ce", 141.9092504)
            },
            new Isotope[] {
                    new Isotope(141, "Pr", 140.9076576)
            },
            new Isotope[] {
                    new Isotope(142, "Nd", 141.907729),
                    new Isotope(143, "Nd", 142.90982),
                    new Isotope(144, "Nd", 143.910093),
                    new Isotope(145, "Nd", 144.9125793),
                    new Isotope(146, "Nd", 145.9131226),
                    new Isotope(148, "Nd", 147.9168993),
                    new Isotope(150, "Nd", 149.9209022)
            },
            new Isotope[] {
                    new Isotope(145, "Pm", 144.9127559),
                    new Isotope(147, "Pm", 146.915145)
            },
            new Isotope[] {
                    new Isotope(144, "Sm", 143.9120065),
                    new Isotope(147, "Sm", 146.9149044),
                    new Isotope(148, "Sm", 147.9148292),
                    new Isotope(149, "Sm", 148.9171921),
                    new Isotope(150, "Sm", 149.9172829),
                    new Isotope(152, "Sm", 151.9197397),
                    new Isotope(154, "Sm", 153.9222169)
            },
            new Isotope[] {
                    new Isotope(151, "Eu", 150.9198578),
                    new Isotope(153, "Eu", 152.921238)
            },
            new Isotope[] {
                    new Isotope(152, "Gd", 151.9197995),
                    new Isotope(154, "Gd", 153.9208741),
                    new Isotope(155, "Gd", 154.9226305),
                    new Isotope(156, "Gd", 155.9221312),
                    new Isotope(157, "Gd", 156.9239686),
                    new Isotope(158, "Gd", 157.9241123),
                    new Isotope(160, "Gd", 159.9270624)
            },
            new Isotope[] {
                    new Isotope(159, "Tb", 158.9253547)
            },
            new Isotope[] {
                    new Isotope(156, "Dy", 155.9242847),
                    new Isotope(158, "Dy", 157.9244159),
                    new Isotope(160, "Dy", 159.9252046),
                    new Isotope(161, "Dy", 160.9269405),
                    new Isotope(162, "Dy", 161.9268056),
                    new Isotope(163, "Dy", 162.9287383),
                    new Isotope(164, "Dy", 163.9291819)
            },
            new Isotope[] {
                    new Isotope(165, "Ho", 164.9303288)
            },
            new Isotope[] {
                    new Isotope(162, "Er", 161.9287884),
                    new Isotope(164, "Er", 163.9292088),
                    new Isotope(166, "Er", 165.9302995),
                    new Isotope(167, "Er", 166.9320546),
                    new Isotope(168, "Er", 167.9323767),
                    new Isotope(170, "Er", 169.9354702)
            },
            new Isotope[] {
                    new Isotope(169, "Tm", 168.9342179)
            },
            new Isotope[] {
                    new Isotope(168, "Yb", 167.9338896),
                    new Isotope(170, "Yb", 169.9347664),
                    new Isotope(171, "Yb", 170.9363302),
                    new Isotope(172, "Yb", 171.9363859),
                    new Isotope(173, "Yb", 172.9382151),
                    new Isotope(174, "Yb", 173.9388664),
                    new Isotope(176, "Yb", 175.9425764)
            },
            new Isotope[] {
                    new Isotope(175, "Lu", 174.9407752),
                    new Isotope(176, "Lu", 175.9426897)
            },
            new Isotope[] {
                    new Isotope(174, "Hf", 173.9400461),
                    new Isotope(176, "Hf", 175.9414076),
                    new Isotope(177, "Hf", 176.9432277),
                    new Isotope(178, "Hf", 177.9437058),
                    new Isotope(179, "Hf", 178.9458232),
                    new Isotope(180, "Hf", 179.946557)
            },
            new Isotope[] {
                    new Isotope(180, "Ta", 179.9474648),
                    new Isotope(181, "Ta", 180.9479958)
            },
            new Isotope[] {
                    new Isotope(180, "W", 179.9467108),
                    new Isotope(182, "W", 181.94820394),
                    new Isotope(183, "W", 182.95022275),
                    new Isotope(184, "W", 183.95093092),
                    new Isotope(186, "W", 185.9543628)
            },
            new Isotope[] {
                    new Isotope(185, "Re", 184.9529545),
                    new Isotope(187, "Re", 186.9557501)
            },
            new Isotope[] {
                    new Isotope(184, "Os", 183.9524885),
                    new Isotope(186, "Os", 185.953835),
                    new Isotope(187, "Os", 186.9557474),
                    new Isotope(188, "Os", 187.9558352),
                    new Isotope(189, "Os", 188.9581442),
                    new Isotope(190, "Os", 189.9584437),
                    new Isotope(192, "Os", 191.961477)
            },
            new Isotope[] {
                    new Isotope(191, "Ir", 190.9605893),
                    new Isotope(193, "Ir", 192.9629216)
            },
            new Isotope[] {
                    new Isotope(190, "Pt", 189.9599297),
                    new Isotope(192, "Pt", 191.9610387),
                    new Isotope(194, "Pt", 193.9626809),
                    new Isotope(195, "Pt", 194.9647917),
                    new Isotope(196, "Pt", 195.96495209),
                    new Isotope(198, "Pt", 197.9678949)
            },
            new Isotope[] {
                    new Isotope(197, "Au", 196.96656879)
            },
            new Isotope[] {
                    new Isotope(196, "Hg", 195.9658326),
                    new Isotope(198, "Hg", 197.9667686),
                    new Isotope(199, "Hg", 198.96828064),
                    new Isotope(200, "Hg", 199.96832659),
                    new Isotope(201, "Hg", 200.97030284),
                    new Isotope(202, "Hg", 201.9706434),
                    new Isotope(204, "Hg", 203.97349398)
            },
            new Isotope[] {
                    new Isotope(203, "Tl", 202.9723446),
                    new Isotope(205, "Tl", 204.9744278)
            },
            new Isotope[] {
                    new Isotope(204, "Pb", 203.973044),
                    new Isotope(206, "Pb", 205.9744657),
                    new Isotope(207, "Pb", 206.9758973),
                    new Isotope(208, "Pb", 207.9766525)
            },
            new Isotope[] {
                    new Isotope(209, "Bi", 208.9803991)
            },
            new Isotope[] {
                    new Isotope(209, "Po", 208.9824308),
                    new Isotope(210, "Po", 209.9828741)
            },
            new Isotope[] {
                    new Isotope(210, "At", 209.9871479),
                    new Isotope(211, "At", 210.9874966)
            },
            new Isotope[] {
                    new Isotope(211, "Rn", 210.9906011),
                    new Isotope(220, "Rn", 220.0113941),
                    new Isotope(222, "Rn", 222.0175782)
            },
            new Isotope[] {
                    new Isotope(223, "Fr", 223.019736)
            },
            new Isotope[] {
                    new Isotope(223, "Ra", 223.0185023),
                    new Isotope(224, "Ra", 224.020212),
                    new Isotope(226, "Ra", 226.0254103),
                    new Isotope(228, "Ra", 228.0310707)
            },
            new Isotope[] {
                    new Isotope(227, "Ac", 227.0277523)
            },
            new Isotope[] {
                    new Isotope(230, "Th", 230.0331341),
                    new Isotope(232, "Th", 232.0380558)
            },
            new Isotope[] {
                    new Isotope(231, "Pa", 231.0358842)
            },
            new Isotope[] {
                    new Isotope(233, "U", 233.0396355),
                    new Isotope(234, "U", 234.0409523),
                    new Isotope(235, "U", 235.0439301),
                    new Isotope(236, "U", 236.0455682),
                    new Isotope(238, "U", 238.0507884)
            },
            new Isotope[] {
                    new Isotope(236, "Np", 236.04657),
                    new Isotope(237, "Np", 237.0481736)
            },
            new Isotope[] {
                    new Isotope(238, "Pu", 238.0495601),
                    new Isotope(239, "Pu", 239.0521636),
                    new Isotope(240, "Pu", 240.0538138),
                    new Isotope(241, "Pu", 241.0568517),
                    new Isotope(242, "Pu", 242.0587428),
                    new Isotope(244, "Pu", 244.0642053)
            },
            new Isotope[] {
                    new Isotope(241, "Am", 241.0568293),
                    new Isotope(243, "Am", 243.0613813)
            },
            new Isotope[] {
                    new Isotope(243, "Cm", 243.0613893),
                    new Isotope(244, "Cm", 244.0627528),
                    new Isotope(245, "Cm", 245.0654915),
                    new Isotope(246, "Cm", 246.0672238),
                    new Isotope(247, "Cm", 247.0703541),
                    new Isotope(248, "Cm", 248.0723499)
            },
            new Isotope[] {
                    new Isotope(247, "Bk", 247.0703073),
                    new Isotope(249, "Bk", 249.0749877)
            },
            new Isotope[] {
                    new Isotope(249, "Cf", 249.0748539),
                    new Isotope(250, "Cf", 250.0764062),
                    new Isotope(251, "Cf", 251.0795886),
                    new Isotope(252, "Cf", 252.0816272)
            },
            new Isotope[] {
                    new Isotope(252, "Es", 252.08298)
            },
            new Isotope[] {
                    new Isotope(257, "Fm", 257.0951061)
            },
            new Isotope[] {
                    new Isotope(258, "Md", 258.0984315),
                    new Isotope(260, "Md", 260.10365)
            },
            new Isotope[] {
                    new Isotope(259, "No", 259.10103)
            },
            new Isotope[] {
                    new Isotope(262, "Lr", 262.10961)
            },
            new Isotope[] {
                    new Isotope(267, "Rf", 267.12179)
            },
            new Isotope[] {
                    new Isotope(268, "Db", 268.12567)
            },
            new Isotope[] {
                    new Isotope(271, "Sg", 271.13393)
            },
            new Isotope[] {
                    new Isotope(272, "Bh", 272.13826)
            },
            new Isotope[] {
                    new Isotope(270, "Hs", 270.13429)
            },
            new Isotope[] {
                    new Isotope(276, "Mt", 276.15159)
            },
            new Isotope[] {
                    new Isotope(281, "Ds", 281.16451)
            },
            new Isotope[] {
                    new Isotope(280, "Rg", 280.16514)
            },
            new Isotope[] {
                    new Isotope(285, "Cn", 285.17712)
            },
            new Isotope[] {
                    new Isotope(284, "Nh", 284.17873)
            },
            new Isotope[] {
                    new Isotope(289, "Fl", 289.19042)
            },
            new Isotope[] {
                    new Isotope(288, "Mc", 288.19274)
            },
            new Isotope[] {
                    new Isotope(293, "Lv", 293.20449)
            },
            new Isotope[] {
                    new Isotope(292, "Ts", 292.20746)
            },
            new Isotope[] {
                    new Isotope(294, "Og", 294.21392)
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
