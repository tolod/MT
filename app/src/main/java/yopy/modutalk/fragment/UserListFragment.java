package yopy.modutalk.fragment;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;


import yopy.modutalk.MainActivity;
import yopy.modutalk.R;
import yopy.modutalk.chat.ChatActivity;
import yopy.modutalk.common.FirestoreAdapter;
import yopy.modutalk.common.Util9;
import yopy.modutalk.model.UserModel;



import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.mediation.customevent.CustomEventAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.google.firebase.firestore.FirebaseFirestore.getInstance;

public class UserListFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirestoreAdapter firestoreAdapter;
    private InterstitialAd mInterstitialAd;
    private FirebaseFirestore mFireStore;
    private List usersList;
    private RecyclerView mMainList;
    ImageButton mSearchBtn;
    EditText mSearchField;
    String name;

    public UserListFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getInstance();


        MobileAds.initialize(getContext(),
                "ca-app-pub-9778515385069911/4557972785");
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-9778515385069911/4557972785");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }


    //SwipeController swipeController = null;


    @Override
    public void onStart() {
        super.onStart();

        if (firestoreAdapter != null) {
            firestoreAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firestoreAdapter != null) {
            firestoreAdapter.stopListening();
        }
    }

    private StorageReference storageReference;

    private void status(String status) {

        storageReference = FirebaseStorage.getInstance().getReference("users");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
    }

    @Override
    public void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userlist, container, false);

        //firestoreAdapter = new RecyclerViewAdapter(getInstance().collection("users").orderBy("usernm"));
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager((inflater.getContext())));
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new UserFragmentRecyclerViewAdapter());
        //firestoreAdapter = new RecyclerView.Adapter<>(getInstance().collection("users").orderBy("usernm"));
        // recyclerView.setAdapter(firestoreAdapter);

        //mFireStore = FirebaseFirestore.getInstance();
        // mSearchField = (EditText) view.findViewById(R.id.search_field);
        // mSearchBtn = (ImageButton) view.findViewById(R.id.search_btn);

        //mSearchBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {

        // SearchUserFirebase();
        //    }
        // });


        // swipeController = new SwipeController(new SwipeControllerActions() {
        // @Override
        // public void onRightClicked(int position) {
        //firestoreAdapter.getSnapshot(position).getReference().delete();
        //  firestoreAdapter.notifyItemRemoved(position);
        //  firestoreAdapter.notifyItemRangeChanged(position, firestoreAdapter.getItemCount());
        // }
        // });
        // ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        //itemTouchhelper.attachToRecyclerView(recyclerView);

        // recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        //   @Override
        //  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //      swipeController.onDraw(c);
        // }
        //});

        return view;
    }




   /* private void SearchUserFirebase(){
        name=mSearchField.getText().toString();
        if(!name.isEmpty()){
            Query query = mFireStore.collection("users").orderBy("usernm").startAt(name).endAt(name+"\uf8ff");
query.addSnapshotListener(new EventListener<QuerySnapshot>() {
    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e !=null){
            Log.d("TAG","Error:"+e.getMessage());
        }
        ArrayList usersList = new ArrayList();

        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
            if(doc.getType() == DocumentChange.Type.ADDED){
                UserModel user=doc.getDocument().toObject(UserModel.class);
                usersList.add(user);
                firestoreAdapter.notifyDataSetChanged();

            }

        }
    }
});

        }

}*/


/*
    private void SearchUserFirebase(){
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        rootRef.collection("users").whereEqualTo("usernm", "asd").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.getString("asd").equals("Yes")) {
                            Log.d(TAG, "User is Admin!");
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }
*/

    class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<UserModel> userModels;
        final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(60));
        private StorageReference storageReference;
        private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();



        public UserFragmentRecyclerViewAdapter() {

            storageReference = FirebaseStorage.getInstance().getReference();

            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            final DocumentReference docref = db.collection("users").document("usernm");
            docref.collection("users").orderBy("usernm")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {

                            userModels = new ArrayList<UserModel>();
                            userModels.clear();
                            for (DocumentSnapshot doc : snapshots) {
                                UserModel userModel = doc.toObject(UserModel.class);
                                if(myUid.equals(userModel.getUid())) continue;
                                userModels.add(userModel);
                            }
                            notifyDataSetChanged();
                        }

                    });
        }



        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //DocumentSnapshot documentSnapshot = getSnapshot(position);
            userModels = new ArrayList<>();
            final UserModel user = userModels.get(position);
            //final UserModel user = documentSnapshot.toObject(UserModel.class);
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            customViewHolder.user_name.setText(user.getUsernm());
            customViewHolder.user_msg.setText(user.getUsermsg());


            if(myUid.equals(user.getUid())) {
                customViewHolder.itemView.setVisibility(View.INVISIBLE);
                customViewHolder.itemView.getLayoutParams().height = 0;
                return;
            }
            customViewHolder.user_name.setText(user.getUsernm());
            customViewHolder.user_msg.setText(user.getUsermsg());


            if (user.getUserphoto() == null) {
                Glide.with(getActivity()).load(R.drawable.user)
                        .apply(requestOptions)
                        .into(customViewHolder.user_photo);

            } else {
                Glide.with(getActivity())
                        .load(storageReference.child("userPhoto/" + user.getUserphoto()))
                        .apply(requestOptions)
                        .into(customViewHolder.user_photo);
            }


            // 여기 위에 부분 활용하여, 밑에 클릭 리스너에 바로 ChatActivity 로 가게 되어있는데 프로필 상세 ACTIVITY 적용가능.


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getView().getContext(), ChatActivity.class);
                    intent.putExtra("toUid", user.getUid());
                    startActivity(intent);
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    //displayAD();


                }
            });
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }
    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView user_photo;
        public TextView user_name;
        public TextView user_msg;
        //private ImageView img_on;
        //private ImageView img_off;


        public CustomViewHolder(View view) {
            super(view);
            user_photo = view.findViewById(R.id.user_photo);
            user_name = view.findViewById(R.id.user_name);
            user_msg = view.findViewById(R.id.user_msg);
            //img_on=view.findViewById(R.id.img_on);
            //img_off=view.findViewById(R.id.img_off);
        }
    }
}