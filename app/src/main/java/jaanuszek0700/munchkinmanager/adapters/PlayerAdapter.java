package jaanuszek0700.munchkinmanager.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import jaanuszek0700.munchkinmanager.R;
import jaanuszek0700.munchkinmanager.models.Player;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {

    private List<Player> players;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public PlayerAdapter(Context context, List<Player> players) {
        this.mInflater = LayoutInflater.from(context);
        this.players = players;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_player, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Player player = players.get(position);
        holder.username.setText(player.getUsername());
        holder.level.setText(String.valueOf(player.getLevel()));
        holder.power.setText(String.valueOf(player.getPower()));

        int imageIdentifier = mInflater.getContext().getResources().getIdentifier(player.getImage(), "drawable", "jaanuszek0700.munchkinmanager");
        if (imageIdentifier > 0)
            holder.image.setImageResource(imageIdentifier);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return players.size();
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView username, level,  power;
        ImageView image;
        Typeface font1 = Typeface.createFromAsset(mInflater.getContext().getAssets(), "fonts/Windlass.ttf");
        Typeface font2 = Typeface.createFromAsset(mInflater.getContext().getAssets(), "fonts/CaslonAntique.ttf");

        ViewHolder(View itemView) {
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.player_username);
            level = (TextView) itemView.findViewById(R.id.text_level);
            power = (TextView) itemView.findViewById(R.id.text_power);
            image = (ImageView) itemView.findViewById(R.id.player_image);

            username.setTypeface(font1);
            level.setTypeface(font2);
            power.setTypeface(font2);

            ((TextView) itemView.findViewById(R.id.textView_level)).setTypeface(font2);
            ((TextView) itemView.findViewById(R.id.textView_power)).setTypeface(font2);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Player getItem(int id) {
        return players.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
