package sn.ept.git.mobile.ui.employe;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sn.ept.git.mobile.api.ClientAPI;
import sn.ept.git.mobile.api.interfaces.ClientCall;
import sn.ept.git.mobile.api.interfaces.EmployeCall;
import sn.ept.git.mobile.api.models.client.Client;
import sn.ept.git.mobile.api.models.employe.Employe;

public class EmployeViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Employe>> employeList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> getEmployesError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postEmployeError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateEmployeError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteEmployeError = new MutableLiveData<>();
    private String errorMessage;

    public EmployeViewModel() {
    }

    public void getEmployes() {
        EmployeCall employeCall = ClientAPI.getEmployeCall();
        Call<List<Employe>> call = employeCall.getEmployes();
        call.enqueue(new Callback<List<Employe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Employe>> call, @NonNull Response<List<Employe>> response) {
                if (response.isSuccessful()) {
                    List<Employe> employes = response.body();
                    employeList.setValue(employes);
                } else {
                    getEmployesError.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Employe>> call, @NonNull Throwable t) {
                Log.e("ERROR", t.getMessage());
                getEmployesError.setValue(true);
            }
        });
    }

    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<List<Employe>> getEmployeList() {
        return employeList;
    }

    public MutableLiveData<Boolean> getGetEmployesError() {
        return getEmployesError;
    }

    public MutableLiveData<Boolean> getPostEmployeError() {
        return postEmployeError;
    }

    public MutableLiveData<Boolean> getUpdateEmployeError() {
        return updateEmployeError;
    }

    public MutableLiveData<Boolean> getDeleteEmployeError() {
        return deleteEmployeError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setText(String s) {
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<String> getText() {
        return mutableLiveData;
    }
}