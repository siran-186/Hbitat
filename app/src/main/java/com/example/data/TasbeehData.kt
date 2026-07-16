package com.example.data

data class TasbeehDua(
    val dayNumber: Int,
    val title: String,
    val arabic: String,
    val translation: String,
    val source: String
)

object TasbeehData {
    val items = listOf(
        TasbeehDua(
            dayNumber = 1,
            title = "Relief from Anxiety & Laziness",
            arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحُزْنِ، وَالْعَجْزِ وَالْكَسَلِ",
            translation = "اے اللہ! میں پریشانی، غم، عاجزی اور سستی سے تیری پناہ مانگتا ہوں",
            source = "Sahih al-Bukhari"
        ),
        TasbeehDua(
            dayNumber = 2,
            title = "Dua of Prophet Musa (AS)",
            arabic = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِنْ لِسَانِي يَفْقَهُوا قَوْلِي",
            translation = "اے میرے رب! میرا سینہ کھول دے، اور میرے کام کو میرے لیے آسان کر دے، اور میری زبان کی گرہ سلجھا دے تاکہ وہ میری بات سمجھ سکیں",
            source = "Surah Ta-Ha, 20:25-28"
        ),
        TasbeehDua(
            dayNumber = 3,
            title = "Dua for Guidance & Steadfastness",
            arabic = "اللَّهُمَّ اهْدِنِي وَسَدِّدْنِي",
            translation = "اے اللہ! مجھے ہدایت دے اور مجھے سیدھی راہ پر قائم رکھ",
            source = "Sahih Muslim"
        ),
        TasbeehDua(
            dayNumber = 4,
            title = "Dua for Any Goodness",
            arabic = "رَبِّ إِنِّي لِمَا أَنْزَلْتَ إِلَيَّ مِنْ خَيْرٍ فَقِيرٌ",
            translation = "اے میرے رب! تو جو بھی بھلائی میری طرف نازل کرے، میں اس کا محتاج ہوں",
            source = "Surah Al-Qasas, 28:24"
        ),
        TasbeehDua(
            dayNumber = 5,
            title = "Dua for Good in Both Worlds",
            arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            translation = "اے ہمارے رب! ہمیں دنیا میں بھی بھلائی دے اور آخرت میں بھی بھلائی عطا فرما، اور ہمیں دوزخ کے عذاب سے بچا",
            source = "Surah Al-Baqarah, 2:201"
        ),
        TasbeehDua(
            dayNumber = 6,
            title = "Dua of Prophet Yunus (AS)",
            arabic = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            translation = "تیرے سوا کوئی معبود نہیں، تو پاک ہے، بے شک میں ہی قصورواروں میں سے تھا",
            source = "Surah Al-Anbya, 21:87"
        ),
        TasbeehDua(
            dayNumber = 7,
            title = "Dua of Prophet Ayyub (AS)",
            arabic = "أَنِّي مَسَّنِيَ الضُّرُّ وَأَنتَ أَرْحَمُ الرَّاحِمِينَ",
            translation = "مجھے تکلیف پہنچی ہے، اور تو سب سے بڑھ کر رحم کرنے والا ہے",
            source = "Surah Al-Anbya, 21:83"
        ),
        TasbeehDua(
            dayNumber = 8,
            title = "Dua for Knowledge",
            arabic = "رَّبِّ زِدْنِي عِلْمًا",
            translation = "اے میرے رب! میرے علم میں اضافہ فرما",
            source = "Surah Ta-Ha, 20:114"
        ),
        TasbeehDua(
            dayNumber = 9,
            title = "Dua for Steadfast Hearts",
            arabic = "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِنْ لَدُنْكَ رَحْمَةً",
            translation = "اے ہمارے رب! ہدایت دینے کے بعد ہمارے دلوں کو ٹیڑھا نہ ہونے دینا اور ہمیں اپنے پاس سے رحمت عطا فرما",
            source = "Surah Ali 'Imran, 3:8"
        ),
        TasbeehDua(
            dayNumber = 10,
            title = "Dua for Patience & Strength",
            arabic = "رَبَّنَا أَفْرِغْ عَلَيْنَا صَبْرًا وَثَبِّتْ أَقْدَامَنَا وَانْصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ",
            translation = "اے ہمارے رب! ہم پر صبر انڈیل دے، ہمارے قدموں کو جما دے اور کافروں کے مقابلے میں ہماری مدد فرما",
            source = "Surah Al-Baqarah, 2:250"
        ),
        TasbeehDua(
            dayNumber = 11,
            title = "Dua for Family Blessing",
            arabic = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            translation = "اے ہمارے رب! ہمیں ہماری بیویوں اور اولاد سے آنکھوں کی ٹھنڈک عطا فرما اور ہمیں پرہیزگاروں کا پیشوا بنا",
            source = "Surah Al-Furqan, 25:74"
        ),
        TasbeehDua(
            dayNumber = 12,
            title = "Dua for Establishing Prayers",
            arabic = "رَبِّ اجْعَلْنِي مُقِيمَ الصَّلَاةِ وَمِنْ ذُرِّيَّتِي ۚ رَبَّنَا وَتَقَبَّلْ دُعَاءِ",
            translation = "اے میرے رب! مجھے اور میری اولاد کو نماز قائم کرنے والا بنا، اے ہمارے رب! اور میری دعا قبول فرما",
            source = "Surah Ibrahim, 14:40"
        ),
        TasbeehDua(
            dayNumber = 13,
            title = "Dua of Adam (AS) & Hawa",
            arabic = "رَبَّنَا ظَلَمْنَا أَنْفُسَنَا وَإِنْ لَمْ تَغْفِرْ لَنَا وَتَرْحَمْنَا لَنَكُونَنَّ مِنَ الْخَاسِرِينَ",
            translation = "اے ہمارے رب! ہم نے اپنی جانوں پر ظلم کیا، اور اگر تو نے ہمیں معاف نہ فرمایا اور ہم پر رحم نہ کیا تو ہم یقیناً نقصان اٹھانے والوں میں سے ہو جائیں گے",
            source = "Surah Al-A'raf, 7:23"
        ),
        TasbeehDua(
            dayNumber = 14,
            title = "Dua for Parents",
            arabic = "رَّبِّ ارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            translation = "اے میرے رب! ان دونوں (والدین) پر رحم فرما جس طرح انہوں نے بچپن میں میری پرورش کی",
            source = "Surah Al-Isra, 17:24"
        ),
        TasbeehDua(
            dayNumber = 15,
            title = "Dua for Mercy & Guidance",
            arabic = "رَبَّنَا آتِنَا مِن لَّدُنكَ رَحْمَةً وَهَيِّئْ لَنَا مِنْ أَمْرِنَا رَشَدًا",
            translation = "اے ہمارے رب! ہمیں اپنے پاس سے رحمت عطا فرما اور ہمارے معاملے میں ہمارے لیے ہدایت کا سامان درست کر دے",
            source = "Surah Al-Kahf, 18:10"
        ),
        TasbeehDua(
            dayNumber = 16,
            title = "Dua for Forgiveness & Mercy",
            arabic = "رَبِّ اغْفِرْ وَارْحَمْ وَأَنْتَ خَيْرُ الرَّاحِمِينَ",
            translation = "اے میرے رب! بخش دے اور رحم فرما، اور تو سب سے بہتر رحم کرنے والا ہے",
            source = "Surah Al-Mu'minun, 23:118"
        ),
        TasbeehDua(
            dayNumber = 17,
            title = "Dua for Wellness & Pardon",
            arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ",
            translation = "اے اللہ! میں تجھ سے دنیا اور آخرت میں معافی اور عافیت کا سوال کرتا ہوں",
            source = "Sunan Ibn Majah"
        )
    )
}
