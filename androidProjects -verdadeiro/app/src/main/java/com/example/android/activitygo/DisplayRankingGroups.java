package com.example.android.activitygo;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.activitygo.model.RankingGroups;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayRankingGroups extends Fragment {


    private DatabaseReference databaseRankingsGrupos;

    public DisplayRankingGroups() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_display_ranking_groups, container, false);

        databaseRankingsGrupos = FirebaseDatabase.getInstance().getReference("rankingGrupos");

        databaseRankingsGrupos.addListenerForSingleValueEvent(new ValueEventListener() {
            RankingGroups r;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    r = child.getValue(RankingGroups.class);
                }

                TextView tv =getActivity().findViewById(R.id.TESTE);
                tv.setText(""+r.getGruposRanking().get("Teste"));
                Toast.makeText(getContext(),""+r.getGruposRanking().get("Teste"),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        return v;
    }

}
