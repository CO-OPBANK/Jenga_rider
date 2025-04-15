package ke.co.coopbank.financialliteracy.ui.activity.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import dagger.hilt.android.AndroidEntryPoint;
import ke.co.coopbank.financialliteracy.databinding.ActivityAuthBinding;


@AndroidEntryPoint
public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAuthBinding binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}