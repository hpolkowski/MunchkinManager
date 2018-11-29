package jaanuszek0700.munchkinmanager.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jaanuszek0700.munchkinmanager.R;
import jaanuszek0700.munchkinmanager.models.Game;

public class GameAdapter extends ArrayAdapter<Game> {
    private final LayoutInflater inflater;

    public GameAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_list_item_1);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Game getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Game game = (Game) getItem(position);
        View rowView = inflater.inflate(R.layout.adapter_game, parent, false);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Windlass.ttf");
        TextView gameNameTextView = (TextView) rowView.findViewById(R.id.game_name);

        gameNameTextView.setTypeface(font);
        gameNameTextView.setText(game.getName());

        return rowView;
    }
}
