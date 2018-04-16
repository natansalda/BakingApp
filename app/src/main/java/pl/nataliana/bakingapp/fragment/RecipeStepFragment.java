package pl.nataliana.bakingapp.fragment;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import pl.nataliana.bakingapp.R;
import pl.nataliana.bakingapp.databinding.FragmentRecipeStepBinding;
import pl.nataliana.bakingapp.model.RecipeStep;

/**
 * Created by natalia.nazaruk on 21.03.2018.
 */

public class RecipeStepFragment extends Fragment implements ExoPlayer.EventListener {

    private long playbackPosition;
    private boolean playbackReady = true;
    private int currentWindow;
    private static final String PLAYER_POSITION = "playback_position";
    private static final String PLAYBACK_READY = "playback_ready";
    private final String TAG = RecipeStepFragment.class.getSimpleName();
    private static final String BUNDLE_STEP_DATA = "pl.nataliana.bakingapp.step_data";
    private FragmentRecipeStepBinding binding;
    private RecipeStep mRecipeStep;
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    public static RecipeStepFragment newInstance(RecipeStep step) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_STEP_DATA, step);
        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYER_POSITION);
            playbackReady = savedInstanceState.getBoolean(PLAYBACK_READY);
        }

        if ((arguments != null) && (arguments.containsKey(BUNDLE_STEP_DATA))) {
            mRecipeStep = arguments.getParcelable(BUNDLE_STEP_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_step, container, false);
        final View view = binding.getRoot();

        if (!getResources().getBoolean(R.bool.isTablet)) {
            binding.tbToolbar.toolbar.setVisibility(View.GONE);
        }

        if (mRecipeStep.getVideoURL() != null && !mRecipeStep.getVideoURL().matches("")) {
            initializeMediaSession();
            initializePlayer(Uri.parse(mRecipeStep.getVideoURL()));
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                    !getResources().getBoolean(R.bool.isTablet)) {
                hideSystemUI();
                binding.tvStepFragmentDirections.setVisibility(View.GONE);
                binding.exoStepFragmentPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                binding.exoStepFragmentPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                binding.tvStepFragmentDirections.setText(mRecipeStep.getDescription());
            }
        } else {
            binding.exoStepFragmentPlayerView.setVisibility(View.GONE);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE &&
                    !getResources().getBoolean(R.bool.isTablet)) {
                hideSystemUI();
                binding.tvStepFragmentDirections.setVisibility(View.GONE);
                binding.ivStepFragmentNoVideoPlaceholder.setVisibility(View.VISIBLE);
            } else {
                binding.tvStepFragmentDirections.setText(mRecipeStep.getDescription());
            }
        }
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible()) {
            if (!isVisibleToUser) {
                if (mExoPlayer != null) {
                    mExoPlayer.setPlayWhenReady(false);
                }
            }
        }
    }

    private void hideSystemUI() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            binding.exoStepFragmentPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);
            String userAgent = Util.getUserAgent(getContext(), "RecipeStepVideo");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(playbackReady);
            mExoPlayer.seekTo(currentWindow, playbackPosition);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            playbackPosition = mExoPlayer.getCurrentPosition();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
            playbackReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            playbackPosition = mExoPlayer.getCurrentPosition();
            playbackReady = mExoPlayer.getPlayWhenReady();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
        }
        outState.putLong(PLAYER_POSITION, playbackPosition);
        outState.putBoolean(PLAYBACK_READY, playbackReady);
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
