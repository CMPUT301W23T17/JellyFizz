package com.example.qr_code_hunter;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.qr_code_hunter.databinding.FragmentBottomSheetDialogBinding;
import com.example.qr_code_hunter.databinding.BottomSheetListBinding;
import java.util.ArrayList;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     BottomSheetDialog.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class BottomSheetDialog extends BottomSheetDialogFragment {

    private static final String ARG_ITEMS = "items";
    private BottomSheetListBinding binding;

    public static BottomSheetDialog newInstance(ArrayList<NearbyCode> items) {
        BottomSheetDialog fragment = new BottomSheetDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ITEMS, items);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        assert getArguments() != null;
        ArrayList<NearbyCode> items = getArguments().getParcelableArrayList(ARG_ITEMS);
        recyclerView.setAdapter(new ItemAdapter(items));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView codeName;
        private final TextView location;
        private final TextView point;
        private final TextView distance;

        ViewHolder(FragmentBottomSheetDialogBinding binding) {
            super(binding.getRoot());
            codeName = binding.codeName;
            distance = binding.distance;
            point = binding.score;
            location = binding.location;
        }
    }

    private static class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private ArrayList<NearbyCode> mItems;    private final ArrayList<NearbyCode> mNearbyCodes;

        ItemAdapter(ArrayList<NearbyCode> nearbyCodes) {
            mNearbyCodes = nearbyCodes;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(FragmentBottomSheetDialogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            NearbyCode nearbyCode = mNearbyCodes.get(position);
            holder.codeName.setText(nearbyCode.getCodeName());
            holder.location.setText(nearbyCode.getLocation());
            holder.point.setText(String.valueOf(nearbyCode.getPoint()));
            holder.distance.setText(String.valueOf(nearbyCode.getDistance()));
        }

        @Override
        public int getItemCount() {
            return mNearbyCodes.size();
        }
    }
}