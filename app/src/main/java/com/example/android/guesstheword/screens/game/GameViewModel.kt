package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// view models hold data and survive configuration changes (e.g: rotate the phone)
// it replaces the onSaveInstanceState as we cannot add much data to the bundle and viewModels has no restrictions of size
// however, view models are destroyed when fragments/activies associated to it are finally destroyed
// so the data is still destroyed when the OS destroys the app
// just before view models are destroyed there is a callback onCleared which is called
// the UIController knows about the model but the model doesn't know about the controller
class GameViewModel : ViewModel() {

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 10000L
    }

    // The live data does not update the fragment if the fragment is not on the screen
    // However, if the data changed while the fragments was off the screen and then the fragment comes back it will take this last updated value of the live data
    // With live data we don't need to clean it on onDestroy as it is automatically destroyed
    // So the fragment observes the live data mutations and the live data observes tha fragment lifecycle

    // Difference MutableLiveData and LiveData
    // MutableLiveData can be read and modified
    // LiveData we can read but we cannot modify

    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    // The current score (it is a live data that can be observed by the fragment
    // we have a MutableLiveData that is only accessible by this class
    // and a LiveData that is accessible outside and returns the private field (encapsulation)
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val timer: CountDownTimer

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    // The list of words - the front of the list is the next word to guess
    // mutable live data are always nullable and start with null value
    private lateinit var wordList: MutableList<String>

    init {
        Log.i("GameViewModel", "GameViewModel created!")
        resetList()
        nextWord()

        // initialize live data
        _score.value = 0
        _word.value = ""

        timer = object: CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished/ ONE_SECOND)
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _eventGameFinish.value = true
            }
        }

        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed")

        // Stop the timer
        timer.cancel()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
           resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
        nextWord()
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }
}