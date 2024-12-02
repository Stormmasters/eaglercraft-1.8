package net.lax1dude.eaglercraft.v1_8.profanity_filter;

/**
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class LookAlikeUnicodeConv {

	public static String convertString(String stringIn) {
		char[] ret = null;
		char c1, c2;
		for(int i = 0, l = stringIn.length(); i < l; ++i) {
			c1 = stringIn.charAt(i);
			c2 = convertChar(c1);
			if(c2 != 0) {
				if(ret == null) {
					ret = stringIn.toCharArray();
				}
				ret[i] = c2;
			}
		}
		return ret != null ? new String(ret) : stringIn;
	}

	public static char convertChar(char charIn) {
		switch (charIn) {
		case 9313:
		case 9333:
		case 65298:
			return '2';
		case 9315:
		case 9335:
		case 65300:
			return '4';
		case 9316:
		case 9336:
		case 65301:
			return '5';
		case 9317:
		case 9337:
		case 65302:
			return '6';
		case 9319:
		case 9339:
		case 65304:
			return '8';
		case 9320:
		case 9340:
		case 65305:
			return '9';
		case 192:
		case 193:
		case 194:
		case 195:
		case 196:
		case 197:
		case 256:
		case 258:
		case 260:
		case 461:
		case 478:
		case 480:
		case 506:
		case 512:
		case 514:
		case 550:
		case 570:
		case 913:
		case 1040:
		case 1232:
		case 1234:
		case 7680:
		case 7840:
		case 7842:
		case 7844:
		case 7846:
		case 7848:
		case 7850:
		case 7852:
		case 7854:
		case 7856:
		case 7858:
		case 7860:
		case 7862:
		case 7944:
		case 7945:
		case 7946:
		case 7947:
		case 7948:
		case 7949:
		case 7950:
		case 7951:
		case 8072:
		case 8073:
		case 8074:
		case 8075:
		case 8076:
		case 8077:
		case 8078:
		case 8079:
		case 8120:
		case 8121:
		case 8122:
		case 8123:
		case 8124:
		case 9424:
		case 65313:
			return 'A';
		case 385:
		case 386:
		case 579:
		case 914:
		case 1042:
		case 7682:
		case 7684:
		case 7686:
		case 9425:
		case 65314:
			return 'B';
		case 199:
		case 262:
		case 264:
		case 266:
		case 268:
		case 391:
		case 571:
		case 1057:
		case 1194:
		case 7688:
		case 9426:
		case 65315:
			return 'C';
		case 270:
		case 272:
		case 393:
		case 394:
		case 7690:
		case 7692:
		case 7694:
		case 7696:
		case 7698:
		case 9427:
		case 65316:
			return 'D';
		case 200:
		case 201:
		case 202:
		case 203:
		case 274:
		case 276:
		case 278:
		case 280:
		case 282:
		case 516:
		case 518:
		case 552:
		case 582:
		case 904:
		case 917:
		case 7700:
		case 7702:
		case 7704:
		case 7706:
		case 7708:
		case 7864:
		case 7866:
		case 7868:
		case 7870:
		case 7872:
		case 7874:
		case 7876:
		case 7878:
		case 7960:
		case 7961:
		case 7962:
		case 7963:
		case 7964:
		case 7965:
		case 8136:
		case 8137:
		case 9428:
		case 65317:
			return 'E';
		case 401:
		case 7710:
		case 9429:
		case 65318:
			return 'F';
		case 284:
		case 286:
		case 288:
		case 290:
		case 403:
		case 484:
		case 486:
		case 500:
		case 7712:
		case 9430:
		case 65319:
			return 'G';
		case 292:
		case 294:
		case 542:
		case 7714:
		case 7716:
		case 7718:
		case 7720:
		case 7722:
		case 7976:
		case 7977:
		case 7978:
		case 7979:
		case 7980:
		case 7981:
		case 7982:
		case 7983:
		case 9431:
		case 65320:
			return 'H';
		case 204:
		case 205:
		case 206:
		case 207:
		case 296:
		case 298:
		case 300:
		case 302:
		case 304:
		case 406:
		case 407:
		case 463:
		case 520:
		case 522:
		case 906:
		case 921:
		case 938:
		case 1030:
		case 7724:
		case 7726:
		case 7880:
		case 7882:
		case 7992:
		case 7993:
		case 7994:
		case 7995:
		case 7996:
		case 7997:
		case 7998:
		case 7999:
		case 9432:
		case 65321:
			return 'I';
		case 308:
		case 584:
		case 1032:
		case 9433:
		case 65322:
			return 'J';
		case 310:
		case 408:
		case 488:
		case 922:
		case 1036:
		case 1050:
		case 1178:
		case 1180:
		case 1182:
		case 7728:
		case 7730:
		case 7732:
		case 9434:
		case 65323:
			return 'K';
		case 313:
		case 315:
		case 317:
		case 319:
		case 321:
		case 573:
		case 7734:
		case 7736:
		case 7738:
		case 7740:
		case 9435:
		case 65324:
			return 'L';
		case 924:
		case 7742:
		case 7744:
		case 7746:
		case 9436:
		case 65325:
			return 'M';
		case 209:
		case 323:
		case 325:
		case 327:
		case 413:
		case 504:
		case 544:
		case 925:
		case 7748:
		case 7750:
		case 7752:
		case 7754:
		case 9437:
		case 65326:
			return 'N';
		case 48:
		case 210:
		case 211:
		case 212:
		case 213:
		case 214:
		case 332:
		case 334:
		case 336:
		case 416:
		case 465:
		case 490:
		case 492:
		case 510:
		case 524:
		case 526:
		case 554:
		case 556:
		case 558:
		case 560:
		case 908:
		case 927:
		case 1054:
		case 1254:
		case 7756:
		case 7758:
		case 7760:
		case 7762:
		case 7884:
		case 7886:
		case 7888:
		case 7890:
		case 7892:
		case 7894:
		case 7896:
		case 7898:
		case 7900:
		case 7902:
		case 7904:
		case 7906:
		case 8008:
		case 8009:
		case 8010:
		case 8011:
		case 8012:
		case 8013:
		case 8184:
		case 8185:
		case 9438:
		case 65296:
		case 65327:
			return 'O';
		case 420:
		case 929:
		case 7764:
		case 7766:
		case 8172:
		case 9439:
		case 65328:
			return 'P';
		case 9440:
		case 65329:
			return 'Q';
		case 340:
		case 342:
		case 344:
		case 528:
		case 530:
		case 588:
		case 7768:
		case 7770:
		case 7772:
		case 7774:
		case 9441:
		case 65330:
			return 'R';
		case 36:
		case 346:
		case 348:
		case 350:
		case 352:
		case 536:
		case 1029:
		case 7776:
		case 7778:
		case 7780:
		case 7782:
		case 7784:
		case 9442:
		case 65331:
			return 'S';
		case 354:
		case 356:
		case 358:
		case 428:
		case 430:
		case 538:
		case 574:
		case 932:
		case 7786:
		case 7788:
		case 7790:
		case 7792:
		case 9443:
		case 65332:
			return 'T';
		case 217:
		case 218:
		case 219:
		case 220:
		case 360:
		case 362:
		case 364:
		case 366:
		case 368:
		case 370:
		case 431:
		case 467:
		case 469:
		case 471:
		case 473:
		case 475:
		case 532:
		case 534:
		case 580:
		case 1329:
		case 1357:
		case 7794:
		case 7796:
		case 7798:
		case 7800:
		case 7802:
		case 7908:
		case 7910:
		case 7912:
		case 7914:
		case 7916:
		case 7918:
		case 7920:
		case 9444:
		case 65333:
			return 'U';
		case 1140:
		case 1142:
		case 7804:
		case 7806:
		case 9445:
		case 65334:
			return 'V';
		case 372:
		case 7808:
		case 7810:
		case 7812:
		case 7814:
		case 7816:
		case 9446:
		case 65335:
			return 'W';
		case 935:
		case 1061:
		case 1202:
		case 1276:
		case 1278:
		case 7818:
		case 7820:
		case 9447:
		case 65336:
			return 'X';
		case 221:
		case 374:
		case 376:
		case 435:
		case 562:
		case 590:
		case 933:
		case 939:
		case 7822:
		case 7922:
		case 7924:
		case 7926:
		case 7928:
		case 8025:
		case 8027:
		case 8029:
		case 8031:
		case 8168:
		case 8169:
		case 8170:
		case 8171:
		case 9448:
		case 65337:
			return 'Y';
		case 377:
		case 379:
		case 381:
		case 437:
		case 548:
		case 918:
		case 7824:
		case 7826:
		case 7828:
		case 9449:
		case 65338:
			return 'Z';
		case 64:
		case 224:
		case 225:
		case 226:
		case 227:
		case 228:
		case 229:
		case 257:
		case 259:
		case 261:
		case 462:
		case 479:
		case 481:
		case 507:
		case 513:
		case 515:
		case 551:
		case 940:
		case 945:
		case 1072:
		case 1233:
		case 1235:
		case 7681:
		case 7834:
		case 7841:
		case 7843:
		case 7845:
		case 7847:
		case 7849:
		case 7851:
		case 7853:
		case 7855:
		case 7857:
		case 7859:
		case 7861:
		case 7863:
		case 7936:
		case 7937:
		case 7938:
		case 7939:
		case 7940:
		case 7941:
		case 7942:
		case 7943:
		case 8048:
		case 8049:
		case 8064:
		case 8065:
		case 8066:
		case 8067:
		case 8068:
		case 8069:
		case 8070:
		case 8071:
		case 8112:
		case 8113:
		case 8114:
		case 8115:
		case 8116:
		case 8118:
		case 8119:
		case 9372:
		case 9398:
		case 65345:
			return 'a';
		case 384:
		case 387:
		case 388:
		case 389:
		case 595:
		case 946:
		case 7683:
		case 7685:
		case 7687:
		case 9373:
		case 9399:
		case 65346:
			return 'b';
		case 231:
		case 263:
		case 265:
		case 267:
		case 269:
		case 392:
		case 572:
		case 1089:
		case 7689:
		case 9374:
		case 9400:
		case 65347:
			return 'c';
		case 271:
		case 273:
		case 396:
		case 598:
		case 599:
		case 7691:
		case 7693:
		case 7695:
		case 7697:
		case 7699:
		case 9375:
		case 9401:
		case 65348:
			return 'd';
		case 51:
		case 232:
		case 233:
		case 234:
		case 235:
		case 275:
		case 277:
		case 279:
		case 281:
		case 283:
		case 517:
		case 519:
		case 553:
		case 583:
		case 1239:
		case 7701:
		case 7703:
		case 7705:
		case 7707:
		case 7709:
		case 7865:
		case 7867:
		case 7869:
		case 7871:
		case 7873:
		case 7875:
		case 7877:
		case 7879:
		case 9314:
		case 9334:
		case 9376:
		case 9402:
		case 65299:
		case 65349:
			return 'e';
		case 402:
		case 7711:
		case 9377:
		case 9403:
		case 65350:
			return 'f';
		case 285:
		case 287:
		case 289:
		case 291:
		case 485:
		case 487:
		case 7713:
		case 9378:
		case 9404:
		case 65351:
			return 'g';
		case 293:
		case 295:
		case 543:
		case 614:
		case 7715:
		case 7717:
		case 7719:
		case 7721:
		case 7723:
		case 7830:
		case 9379:
		case 9405:
		case 65352:
			return 'h';
		case 33:
		case 49:
		case 236:
		case 237:
		case 238:
		case 239:
		case 297:
		case 299:
		case 301:
		case 303:
		case 464:
		case 521:
		case 523:
		case 616:
		case 617:
		case 943:
		case 953:
		case 970:
		case 1110:
		case 7725:
		case 7727:
		case 7881:
		case 7883:
		case 9312:
		case 9332:
		case 9380:
		case 9406:
		case 65297:
		case 65353:
			return 'i';
		case 309:
		case 496:
		case 585:
		case 669:
		case 1112:
		case 9381:
		case 9407:
		case 65354:
			return 'j';
		case 311:
		case 312:
		case 409:
		case 489:
		case 954:
		case 1116:
		case 1179:
		case 1181:
		case 1183:
		case 7729:
		case 7731:
		case 7733:
		case 9382:
		case 9408:
		case 65355:
			return 'k';
		case 314:
		case 316:
		case 318:
		case 320:
		case 322:
		case 410:
		case 619:
		case 620:
		case 621:
		case 7735:
		case 7737:
		case 7739:
		case 7741:
		case 9383:
		case 9409:
		case 65356:
			return 'l';
		case 625:
		case 7743:
		case 7745:
		case 7747:
		case 9384:
		case 9410:
		case 65357:
			return 'm';
		case 241:
		case 324:
		case 326:
		case 328:
		case 329:
		case 414:
		case 505:
		case 565:
		case 626:
		case 627:
		case 7749:
		case 7751:
		case 7753:
		case 7755:
		case 9385:
		case 9411:
		case 65358:
			return 'n';
		case 242:
		case 243:
		case 244:
		case 245:
		case 246:
		case 248:
		case 333:
		case 335:
		case 337:
		case 417:
		case 466:
		case 491:
		case 493:
		case 511:
		case 525:
		case 527:
		case 555:
		case 557:
		case 559:
		case 561:
		case 959:
		case 972:
		case 1086:
		case 1255:
		case 7757:
		case 7759:
		case 7761:
		case 7763:
		case 7885:
		case 7887:
		case 7889:
		case 7891:
		case 7893:
		case 7895:
		case 7897:
		case 7899:
		case 7901:
		case 7903:
		case 7905:
		case 7907:
		case 8000:
		case 8001:
		case 8002:
		case 8003:
		case 8004:
		case 8005:
		case 9386:
		case 9412:
		case 65359:
			return 'o';
		case 421:
		case 961:
		case 7765:
		case 7767:
		case 9387:
		case 9413:
		case 65360:
			return 'p';
		case 587:
		case 672:
		case 9388:
		case 9414:
		case 65361:
			return 'q';
		case 341:
		case 343:
		case 345:
		case 529:
		case 531:
		case 589:
		case 7769:
		case 7771:
		case 7773:
		case 7775:
		case 9389:
		case 9415:
		case 65362:
			return 'r';
		case 347:
		case 349:
		case 351:
		case 353:
		case 537:
		case 575:
		case 1109:
		case 7777:
		case 7779:
		case 7781:
		case 7783:
		case 7785:
		case 9390:
		case 9416:
		case 65363:
			return 's';
		case 55:
		case 355:
		case 357:
		case 359:
		case 427:
		case 429:
		case 539:
		case 566:
		case 964:
		case 1090:
		case 1197:
		case 7787:
		case 7789:
		case 7791:
		case 7793:
		case 7831:
		case 9318:
		case 9338:
		case 9391:
		case 9417:
		case 65303:
		case 65364:
			return 't';
		case 249:
		case 250:
		case 251:
		case 252:
		case 361:
		case 363:
		case 365:
		case 367:
		case 369:
		case 371:
		case 432:
		case 468:
		case 470:
		case 472:
		case 474:
		case 476:
		case 533:
		case 535:
		case 649:
		case 965:
		case 973:
		case 7795:
		case 7797:
		case 7799:
		case 7801:
		case 7803:
		case 7909:
		case 7911:
		case 7913:
		case 7915:
		case 7917:
		case 7919:
		case 7921:
		case 8016:
		case 8017:
		case 8018:
		case 8019:
		case 8020:
		case 8021:
		case 8022:
		case 8023:
		case 8160:
		case 8161:
		case 8162:
		case 8163:
		case 9392:
		case 9418:
		case 65365:
			return 'u';
		case 1141:
		case 1143:
		case 7805:
		case 7807:
		case 9393:
		case 9419:
		case 65366:
			return 'v';
		case 373:
		case 7809:
		case 7811:
		case 7813:
		case 7815:
		case 7817:
		case 7832:
		case 9394:
		case 9420:
		case 65367:
			return 'w';
		case 215:
		case 967:
		case 1093:
		case 1203:
		case 1277:
		case 1279:
		case 7819:
		case 7821:
		case 9395:
		case 9421:
		case 65368:
			return 'x';
		case 253:
		case 255:
		case 375:
		case 436:
		case 563:
		case 591:
		case 947:
		case 1118:
		case 7823:
		case 7833:
		case 7923:
		case 7925:
		case 7927:
		case 7929:
		case 7935:
		case 9396:
		case 9422:
		case 65369:
			return 'y';
		case 378:
		case 380:
		case 382:
		case 438:
		case 549:
		case 576:
		case 656:
		case 657:
		case 7825:
		case 7827:
		case 7829:
		case 9397:
		case 9423:
		case 65370:
			return 'z';
		}
		return 0;
	}

}
