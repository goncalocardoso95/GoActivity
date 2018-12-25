package com.example.android.activitygo;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.activitygo.model.Grupo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ProcuraGrupos extends Fragment {

    private ArrayAdapter<String> listViewAdapter;
    private DatabaseReference databaseGrupo;
    private Dialog dialogResultadoInexistente;
    private String username;
    private Dialog dialogWrongPassword;
    private ArrayList<String> listaPessoas;
    private Grupo g;
    private View v;
    private ValueEventListener grupos;

    private final static String TAG = "ProcuraGrupo";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_procura_grupos, container, false);
        databaseGrupo = FirebaseDatabase.getInstance().getReference("grupos");
        dialogResultadoInexistente = new Dialog(getContext());
        dialogWrongPassword = new Dialog(getContext());

        final String pesquisa = getArguments().getString("PESQUISA");
        username = getArguments().getString("USERNAME");

        grupos = databaseGrupo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String valores = "";
                final ArrayList<String> array = new ArrayList<>();
                final ArrayList<Grupo> trying = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    g = userSnapshot.getValue(Grupo.class);
                    if (g.getNome().contains(pesquisa)) {
                        array.add(g.getNome());
                        trying.add(g);
                    }
                }
                if (!array.isEmpty()) {
                    if (getActivity() != null) {
                        ListView listView = (ListView) v.findViewById(R.id.ListaResultados);

                        GrupoListAdapter adapter = new GrupoListAdapter(getContext(), R.layout.adapter_view_layout, trying);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            private ListaDeElementosJuntarGrupo SelectedFragment;

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //Toast.makeText(getContext(),array.get(position),Toast.LENGTH_LONG).show();
                                //confirmJoin(array.get(position))
                                Bundle args = new Bundle();
                                args.putString("NOMEGRUPO", trying.get(position).getNome());
                                args.putString("USERNAME", username);
                                SelectedFragment = new ListaDeElementosJuntarGrupo();
                                SelectedFragment.setArguments(args);
                                FragmentManager fmana = getFragmentManager();
                                FragmentTransaction ftransacti = fmana.beginTransaction();
                                ftransacti.replace(R.id.fragment_container, SelectedFragment, "GroupFragment");
                                ftransacti.commit();
                            }

                        });
                    }

                } else {
                    resultadosInexistentesPopup();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseGrupo.removeEventListener(grupos);
    }

    public void resultadosInexistentesPopup() {
        Button okButton;
        TextView close;
        TextView popupId;
        dialogResultadoInexistente.setContentView(R.layout.popup_password_errada);
        dialogResultadoInexistente.getWindow().getAttributes().windowAnimations = R.style.FadeAnimation;
        okButton = (Button) dialogResultadoInexistente.findViewById(R.id.okButton);
        close = (TextView) dialogResultadoInexistente.findViewById(R.id.txtClose);
        popupId = (TextView) dialogResultadoInexistente.findViewById(R.id.popUpId);
        popupId.setText("\nNão existem resultados possíveis.\nTenta de novo.");
        okButton.setOnClickListener(new View.OnClickListener() {
            private MergeGroupFragment SelectedFragment;

            @Override
            public void onClick(View v) {
                dialogResultadoInexistente.dismiss();
                Bundle args = new Bundle();
                args.putString("USERNAME", username);
                SelectedFragment = new MergeGroupFragment();
                SelectedFragment.setArguments(args);
                FragmentManager fmana = getFragmentManager();
                FragmentTransaction ftransacti = fmana.beginTransaction();
                ftransacti.replace(R.id.fragment_container, SelectedFragment, "GroupFragment");
                ftransacti.commit();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResultadoInexistente.dismiss();
            }
        });
        dialogResultadoInexistente.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogResultadoInexistente.show();
    }


    public void confirmJoin(String grupo) {
        Button okButton;
        TextView close;
        TextView popupId;
        dialogWrongPassword.setContentView(R.layout.popupjuntargrupo);
        dialogWrongPassword.getWindow().getAttributes().windowAnimations = R.style.FadeAnimation;
        okButton = (Button) dialogWrongPassword.findViewById(R.id.okButton);
        Button cancelButton = (Button) dialogWrongPassword.findViewById(R.id.cancelButton);
        close = (TextView) dialogWrongPassword.findViewById(R.id.txtClose);
        popupId = (TextView) dialogWrongPassword.findViewById(R.id.popUpId);
        popupId.setText("\nTem a certeza que se quer juntar a " + grupo);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWrongPassword.dismiss();
            }
        });
        final String group = grupo;

        okButton.setOnClickListener(new View.OnClickListener() {
            private ListaDeElementosJuntarGrupo SelectedFragment;

            @Override
            public void onClick(View v) {
                listaPessoas = new ArrayList<>();
                listaPessoas.add(username);
                databaseGrupo.orderByChild("elementosGrupo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Toast.makeText(getContext(), "OLEOEOEOEOELEL", Toast.LENGTH_SHORT).show();
                            GenericTypeIndicator<Map<String, Object>> t = new GenericTypeIndicator<Map<String, Object>>() {
                            };
                            Map<String, Object> map = dataSnapshot.getValue(t);

                            /*Iterator myVeryOwnIterator = map.keySet().iterator();
                            while(myVeryOwnIterator.hasNext()) {
                                String key=(String)myVeryOwnIterator.next();
                                String value=map.get(key).toString();
                                Toast.makeText(getContext(), "Key: "+key+" Value: "+value, Toast.LENGTH_LONG).show();
                            }*/

                            //databaseGrupo.child(child.getKey()).child("elementosGrupo").setValue(listaPessoas);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(getContext(), "OOIOIOIOIOIOI", Toast.LENGTH_SHORT).show();
                Bundle args = new Bundle();
                args.putString("NOMEGRUPO", group);
                SelectedFragment = new ListaDeElementosJuntarGrupo();
                SelectedFragment.setArguments(args);
                FragmentManager fmana = getFragmentManager();
                FragmentTransaction ftransacti = fmana.beginTransaction();
                ftransacti.replace(R.id.fragment_container, SelectedFragment, "GroupFragment");
                ftransacti.commit();
                dialogWrongPassword.dismiss();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWrongPassword.dismiss();
            }
        });
        dialogWrongPassword.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWrongPassword.show();
    }
}