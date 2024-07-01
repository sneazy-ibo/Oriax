package ani.dantotsu.settings

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ani.dantotsu.R
import ani.dantotsu.databinding.ActivitySettingsAnilistBinding
import ani.dantotsu.initActivity
import ani.dantotsu.navBarHeight
import ani.dantotsu.statusBarHeight
import ani.dantotsu.themes.ThemeManager
import ani.dantotsu.connections.anilist.AnilistMutations
import ani.dantotsu.settings.saving.PrefManager
import ani.dantotsu.settings.saving.PrefName
import kotlinx.coroutines.launch

class SettingsAnilistActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsAnilistBinding
    private lateinit var anilistMutations: AnilistMutations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager(this).applyTheme()
        initActivity(this)
        val context = this
        binding = ActivitySettingsAnilistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        anilistMutations = AnilistMutations()

        binding.apply {
            settingsAnilistLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = statusBarHeight
                bottomMargin = navBarHeight
            }
            binding.anilistSettingsBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            val timeZone = listOf(
                "(GMT-06:00) Central Time",
                "(GMT-05:00) Eastern Time",
                "(GMT-04:00) Atlantic Time",
                "(GMT-01:00) Central Time",
                "(GMT+00:00) London",
                "(GMT+01:00) Berlin",
                "(GMT+04:00) Dubai",
                "(GMT+05:30) India Standard Time",
                "(GMT+06:00) Dhaka",
                "(GMT+07:00) Bangkok",
                "(GMT+09:00) Tokyo",
            )

            val titleLang = listOf(
                "English (Attack on Titan)",
                "Romaji (Shingeki no Kyojin)",
                "Native (進撃の巨人)"
            )

            val staffNameLang = listOf(
                "Romaji, Western Order (Killua Zoldyck)",
                "Romaji (Zoldyck Killua)",
                "Native (キルア=ゾルディック)"
            )

            val currentTitleLang = PrefManager.getVal(PrefName.AnilistTitleLanguage, "ENGLISH")
            val currentStaffNameLang = PrefManager.getVal(PrefName.AnilistStaffNameLanguage, "ENGLISH")

            val titleLangIndex = when (currentTitleLang) {
                "ENGLISH" -> 0
                "ROMAJI" -> 1
                "NATIVE" -> 2
                else -> 0
            }
            val staffNameLangIndex = when (currentStaffNameLang) {
                "ENGLISH" -> 0
                "ROMAJI" -> 1
                "NATIVE" -> 2
                else -> 0
            }

            settingsAnilistTitleLanguage.setText(titleLang[titleLangIndex])
            settingsAnilistTitleLanguage.setAdapter(
                ArrayAdapter(context, R.layout.item_dropdown, titleLang)
            )
            settingsAnilistTitleLanguage.setOnItemClickListener { _, _, i, _ ->
                val selectedLanguage = when (i) {
                    0 -> "ENGLISH"
                    1 -> "ROMAJI"
                    2 -> "NATIVE"
                    else -> "ENGLISH"
                }
                lifecycleScope.launch {
                    anilistMutations.updateSettings(titleLanguage = selectedLanguage)
                    PrefManager.setVal(PrefName.AnilistTitleLanguage, selectedLanguage)
                }
                settingsAnilistTitleLanguage.clearFocus()
            }

            settingsAnilistStaffLanguage.setText(staffNameLang[staffNameLangIndex])
            settingsAnilistStaffLanguage.setAdapter(
                ArrayAdapter(context, R.layout.item_dropdown, staffNameLang)
            )
            settingsAnilistStaffLanguage.setOnItemClickListener { _, _, i, _ ->
                val selectedLanguage = when (i) {
                    0 -> "ENGLISH"
                    1 -> "ROMAJI"
                    2 -> "NATIVE"
                    else -> "ENGLISH"
                }
                lifecycleScope.launch {
                    anilistMutations.updateSettings(staffNameLanguage = selectedLanguage)
                    PrefManager.setVal(PrefName.AnilistStaffNameLanguage, selectedLanguage)
                }
                settingsAnilistStaffLanguage.clearFocus()
            }

            // Fetch and set other settings
            val displayAdultContent = PrefManager.getVal(PrefName.AnilistDisplayAdultContent, false)
            val airingNotifications = PrefManager.getVal(PrefName.AnilistAiringNotifications, false)
            val restrictMessagesToFollowing = PrefManager.getVal(PrefName.AnilistRestrictMessagesToFollowing, false)

            binding.settingsRecyclerView.adapter = SettingsAdapter(
                arrayListOf(
                    Settings(
                        type = 2,
                        name = getString(R.string.airing_notifications),
                        desc = getString(R.string.airing_notifications_desc),
                        icon = R.drawable.ic_round_notifications_active_24,
                        isChecked = airingNotifications,
                        switch = { isChecked, _ ->
                            lifecycleScope.launch {
                                anilistMutations.updateSettings(airingNotifications = isChecked)
                                PrefManager.setVal(PrefName.AnilistAiringNotifications, isChecked)
                            }
                        }
                    ),
                    Settings(
                        type = 2,
                        name = getString(R.string.display_adult_content),
                        desc = getString(R.string.display_adult_content_desc),
                        icon = R.drawable.ic_round_nsfw_24,
                        isChecked = displayAdultContent,
                        switch = { isChecked, _ ->
                            lifecycleScope.launch {
                                anilistMutations.updateSettings(displayAdultContent = isChecked)
                                PrefManager.setVal(PrefName.AnilistDisplayAdultContent, isChecked)
                            }
                        }
                    ),
                    Settings(
                        type = 2,
                        name = getString(R.string.restrict_messages),
                        desc = getString(R.string.restrict_messages_desc),
                        icon = R.drawable.ic_round_lock_open_24,
                        isChecked = restrictMessagesToFollowing,
                        switch = { isChecked, _ ->
                            lifecycleScope.launch {
                                anilistMutations.updateSettings(restrictMessagesToFollowing = isChecked)
                                PrefManager.setVal(PrefName.AnilistRestrictMessagesToFollowing, isChecked)
                            }
                        }
                    ),
                )
            )
            binding.settingsRecyclerView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        }
    }
}