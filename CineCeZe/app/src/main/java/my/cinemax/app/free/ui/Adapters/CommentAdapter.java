package my.cinemax.app.free.ui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import my.cinemax.app.free.R;
import my.cinemax.app.free.entity.Comment;
import com.squareup.picasso.Picasso;
import my.cinemax.app.free.ui.activities.SupportActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tamim on 16/01/2018.
 */


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private List<Comment> commentList= new ArrayList<>();
    private Context context;
    public CommentAdapter(List<Comment> commentList, Context context){
        this.context=context;
        this.commentList=commentList;
    }
    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, null, false);
        viewHolder.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new CommentAdapter.CommentHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder,final int position) {
        holder.text_view_time_item_comment.setText(commentList.get(position).getCreated());

        byte[] data = Base64.decode(commentList.get(position).getContent(), Base64.DEFAULT);
        String Comment_text = "";
        try {
            Comment_text = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Comment_text = commentList.get(position).getContent();
        }

        holder.text_view_name_item_comment.setText(commentList.get(position).getUser());
        Picasso.with(context).load(commentList.get(position).getImage()).error(R.drawable.placeholder_profile).placeholder(R.drawable.placeholder_profile).into(holder.image_view_comment_iten);
        if (!commentList.get(position).getEnabled()){
            holder.text_view_content_item_comment.setText(context.getResources().getString(R.string.comment_hidden));
            holder.text_view_content_item_comment.setTextColor(context.getResources().getColor(R.color.gray));
        }else{
            holder.text_view_content_item_comment.setText(Comment_text);
        }

        holder.image_view_report.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context,  holder.image_view_report);
            popup.getMenuInflater().inflate(R.menu.menu_report, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(context, SupportActivity.class);
                    intent.putExtra("message","Hi Admin, Please check this comment i think should be removed comment id : "+commentList.get(position).getId() );
                    context.startActivity(intent);
                    return true;
                }
            });

            popup.show();
        });

    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentHolder extends RecyclerView.ViewHolder {
        private final TextView text_view_name_item_comment;
        private final TextView text_view_time_item_comment;
        private final TextView text_view_content_item_comment;
        private final CircleImageView image_view_comment_iten;
        private final ImageView image_view_report;
        public CommentHolder(View itemView) {
            super(itemView);
            this.image_view_comment_iten=(CircleImageView) itemView.findViewById(R.id.image_view_comment_iten);
            this.image_view_report=(ImageView) itemView.findViewById(R.id.image_view_report);
            this.text_view_name_item_comment=(TextView) itemView.findViewById(R.id.text_view_name_item_comment);
            this.text_view_time_item_comment=(TextView) itemView.findViewById(R.id.text_view_time_item_comment);
            this.text_view_content_item_comment=(TextView) itemView.findViewById(R.id.text_view_content_item_comment);
        }
    }
}
