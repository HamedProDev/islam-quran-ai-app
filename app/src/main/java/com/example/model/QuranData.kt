package com.example.model

data class Verse(
    val id: Int,
    val surahId: Int,
    val number: Int,
    val arabic: String,
    val english: String,
    val tafsir: String
)

data class Surah(
    val id: Int,
    val name: String,
    val englishName: String,
    val rasm: String,
    val type: String,
    val verses: List<Verse>
)

object QuranRepository {
    val surahs = listOf(
        Surah(
            id = 1,
            name = "Al-Fatiha",
            englishName = "The Opening",
            rasm = "الفاتحة",
            type = "Meccan",
            verses = listOf(
                Verse(
                    id = 1, surahId = 1, number = 1,
                    arabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    english = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
                    tafsir = "Bismillah is the opening keyword of the Quran. It establishes that all action starts in the name of God, seeking His mercy, blessing, and perpetual guidance."
                ),
                Verse(
                    id = 2, surahId = 1, number = 2,
                    arabic = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                    english = "[All] praise is [due] to Allah, Lord of the worlds.",
                    tafsir = "Alhamdulillah acknowledges that all praise, gratitude, and devotion ultimately belong to Allah, who nurtures and sustains every aspect of existence across all dimensions."
                ),
                Verse(
                    id = 3, surahId = 1, number = 3,
                    arabic = "الرَّحْمَٰنِ الرَّحِيمِ",
                    english = "The Entirely Merciful, the Especially Merciful,",
                    tafsir = "Reiterates God's global mercy encompassing all creations (Ar-Rahman) and His specific, intimate mercy reserved for believers navigating spiritual paths (Ar-Rahim)."
                ),
                Verse(
                    id = 4, surahId = 1, number = 4,
                    arabic = "مَالِكِ يَوْمِ الدِّينِ",
                    english = "Sovereign of the Day of Recompense.",
                    tafsir = "Declares Allah's absolute sovereignty on the Day of Judgment, reminding humanity of ultimate accountability and spiritual closure."
                ),
                Verse(
                    id = 5, surahId = 1, number = 5,
                    arabic = "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ",
                    english = "It is You we worship and You we ask for help.",
                    tafsir = "The core covenant of monotheism: dedicating completely sincere worship, submission, and reliance solely to Allah without intermediaries."
                ),
                Verse(
                    id = 6, surahId = 1, number = 6,
                    arabic = "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ",
                    english = "Guide us to the straight path -",
                    tafsir = "The supreme prayer of the soul: seeking constant, steadfast direction along the balanced spiritual path of wisdom, truth, and obedience."
                ),
                Verse(
                    id = 7, surahId = 1, number = 7,
                    arabic = "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ",
                    english = "The path of those upon whom You have bestowed favor, not of those who have earned [Your] anger or of those who are astray.",
                    tafsir = "Clarifies the path as that traveled by prophets, saints, and the righteous, avoiding extremes of conscious rebellion or negligent ignorance."
                )
            )
        ),
        Surah(
            id = 112,
            name = "Al-Ikhlas",
            englishName = "The Sincerity",
            rasm = "الإخلاص",
            type = "Meccan",
            verses = listOf(
                Verse(
                    id = 8, surahId = 112, number = 1,
                    arabic = "قُلْ هُوَ اللَّهُ أَحَدٌ",
                    english = "Say, \"He is Allah, [who is] One,",
                    tafsir = "Establishes absolute monotheism (Tawhid). Allah is singular, indivisible, and unique; there is no multiplicity in His essence or divinity."
                ),
                Verse(
                    id = 9, surahId = 112, number = 2,
                    arabic = "اللَّهُ الصَّمَدُ",
                    english = "Allah, the Eternal Refuge.",
                    tafsir = "Al-Samad means the Independent Being upon whom all dependent beings rely for their existence, needs, and ultimate sustenance."
                ),
                Verse(
                    id = 10, surahId = 112, number = 3,
                    arabic = "لَمْ يَلِدْ وَلَمْ يُولَدْ",
                    english = "He neither begets nor is born,",
                    tafsir = "Rejects any human-like genealogy or physical relationships attributed to divinity. He transcends temporal origins and physical regeneration."
                ),
                Verse(
                    id = 11, surahId = 112, number = 4,
                    arabic = "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ",
                    english = "And there is none co-equal or comparable to Him.\"",
                    tafsir = "Affirms that nothing in creation matches, resembles, or competes with Allah in His attributes, power, or majesty."
                )
            )
        ),
        Surah(
            id = 113,
            name = "Al-Falaq",
            englishName = "The Daybreak",
            rasm = "الفلق",
            type = "Meccan",
            verses = listOf(
                Verse(
                    id = 12, surahId = 113, number = 1,
                    arabic = "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ",
                    english = "Say, \"I seek refuge in the Lord of daybreak",
                    tafsir = "Seeking divine protective security under the authority of Allah who splits the darkness of night to bring forth the hope and truth of daybreak."
                ),
                Verse(
                    id = 13, surahId = 113, number = 2,
                    arabic = "مِن شَرِّ مَا خَلَقَ",
                    english = "From the evil of what He created",
                    tafsir = "Seeking protection against the inherent potentials for harm or corruption residing in worldly physical creations."
                ),
                Verse(
                    id = 14, surahId = 113, number = 3,
                    arabic = "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ",
                    english = "And from the evil of darkness when it settles",
                    tafsir = "Shielding the mind and heart from obscure dangers, hidden plots, or spiritual vulnerabilities that intensify during dark or passive periods."
                ),
                Verse(
                    id = 15, surahId = 113, number = 4,
                    arabic = "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ",
                    english = "And from the evil of the blowers in knots",
                    tafsir = "Protection from covert sorcery, psychological manipulation, toxic relationships, and forces attempting to sever social or spiritual bonds."
                ),
                Verse(
                    id = 16, surahId = 113, number = 5,
                    arabic = "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ",
                    english = "And from the evil of an envier when he envies.\"",
                    tafsir = "Guarding oneself against the destructive currents of envy and malicious thoughts generated by those desiring the removal of God's blessings from others."
                )
            )
        ),
        Surah(
            id = 114,
            name = "An-Nas",
            englishName = "Mankind",
            rasm = "الناس",
            type = "Meccan",
            verses = listOf(
                Verse(
                    id = 17, surahId = 114, number = 1,
                    arabic = "قُلْ أَعُوذُ بِرَبِّ النَّاسِ",
                    english = "Say, \"I seek refuge in the Lord of mankind,",
                    tafsir = "Acknowledges Allah as the supreme Creator, Caretaker, and evolutionary Guide of all humanity across generations."
                ),
                Verse(
                    id = 18, surahId = 114, number = 2,
                    arabic = "مَلِكِ النَّاسِ",
                    english = "The Sovereign of mankind,",
                    tafsir = "Asserts Allah's absolute legislative and governing sovereignty over all human affairs, laws, and systemic structures."
                ),
                Verse(
                    id = 19, surahId = 114, number = 3,
                    arabic = "إِلَٰهِ النَّاسِ",
                    english = "The God of mankind,",
                    tafsir = "Re-emphasizes that Allah alone is the true object of humanity's love, worship, spiritual alignment, and existential focus."
                ),
                Verse(
                    id = 20, surahId = 114, number = 4,
                    arabic = "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ",
                    english = "From the evil of the retreating whisperer -",
                    tafsir = "Guarding key psychological spaces from covert negative suggestions (waswas) that recede and vanish once Allah is mindfully remembered."
                ),
                Verse(
                    id = 21, surahId = 114, number = 5,
                    arabic = "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ",
                    english = "Who whispers [evil] into the breasts of mankind -",
                    tafsir = "Exposes the operational territory of spiritual corruption, which injects doubt, anxiety, and impure thoughts directly into human hearts and minds."
                ),
                Verse(
                    id = 22, surahId = 114, number = 6,
                    arabic = "مِنَ الْجِنَّةِ وَالنَّاسِ",
                    english = "From among the jinn and mankind.\"",
                    tafsir = "Identifies that whisperers and corrupting influences can originate from unseen transcendental dimensions (Jinn) as well as visible human companions."
                )
            )
        )
    )
}
