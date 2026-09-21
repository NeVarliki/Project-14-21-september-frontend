package ru.myitschool.work.ui.screen.home

import ru.myitschool.work.ui.nav.AppDestination

sealed interface HomeAction {
    class Open(val destination: AppDestination) : HomeAction
}
