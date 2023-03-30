package com.example.qr_code_hunter;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.qr_code_hunter.databinding.FragmentCommentDialogListDialogItemBinding;
import com.example.qr_code_hunter.databinding.FragmentCommentDialogListDialogBinding;

import java.util.ArrayList;

/**
 * A fragment that shows a list of comment of each player who have scanned that code
 */
public class CommentDialogFragment extends BottomSheetDialogFragment {
    
    private static final String ARG_ITEMS = "items";
    private static final String ARG_ITEMS2 = "items2";
    private FragmentCommentDialogListDialogBinding binding;


    public static CommentDialogFragment newInstance(ArrayList<CommentSection> comments, ArrayList<CommentSection> players) {
        CommentDialogFragment fragment = new CommentDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ITEMS, comments);
        args.putParcelableArrayList(ARG_ITEMS2, players);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
            // set the peek height to the 16:9 ratio keyline of its parent
            bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommentDialogListDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView comment_btn = view.findViewById(R.id.buttonComment);
        TextView player_btn = view.findViewById(R.id.buttonPLayers);
        RecyclerView recyclerView = view.findViewById(R.id.comment_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Deal with option to see all comments/ players scanned the code
        comment_btn.setSoundEffectsEnabled(true);
        comment_btn.setSoundEffectsEnabled(false);
        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player_btn.setBackgroundColor(Color.parseColor("#ffffff"));
                comment_btn.setBackgroundColor(Color.parseColor("#e0fbfc"));
                assert getArguments() != null;
                ArrayList<CommentSection> items = getArguments().getParcelableArrayList(ARG_ITEMS);
                recyclerView.setAdapter(new CommentDialogFragment.ItemAdapter(items));
            }
        });

        player_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_btn.setBackgroundColor(Color.parseColor("#ffffff"));
                player_btn.setBackgroundColor(Color.parseColor("#e0fbfc"));
                assert getArguments() != null;
                ArrayList<CommentSection> items = getArguments().getParcelableArrayList(ARG_ITEMS2);
                recyclerView.setAdapter(new CommentDialogFragment.ItemAdapter(items));
            }
        });
        comment_btn.performClick();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView username;
        private final TextView comment;

        ViewHolder(@NonNull FragmentCommentDialogListDialogItemBinding binding) {
            super(binding.getRoot());
            username = binding.username;
            comment = binding.comment;
        }
    }

     /**
     * This class acts as an adapter to show comment section list
     */
    private static class ItemAdapter extends RecyclerView.Adapter<CommentDialogFragment.ViewHolder> {

        private ArrayList<CommentSection> mComments;

        ItemAdapter(ArrayList<CommentSection> commentSection) {
            mComments = commentSection;
        }

        @NonNull
        @Override
        public CommentDialogFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CommentDialogFragment.ViewHolder(FragmentCommentDialogListDialogItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(CommentDialogFragment.ViewHolder holder, int position) {
            CommentSection commentSection = mComments.get(position);
            holder.username.setText(commentSection.getUsername());
            if (commentSection.getComment() == null) {
                holder.comment.setVisibility(View.GONE);
            } else {
                holder.comment.setText(commentSection.getComment());
            }
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }
    }
}
