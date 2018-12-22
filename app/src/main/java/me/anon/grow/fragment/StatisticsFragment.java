package me.anon.grow.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;

import me.anon.grow.R;
import me.anon.lib.Views;
import me.anon.lib.helper.StatsHelper;
import me.anon.lib.helper.TimeHelper;
import me.anon.lib.manager.PlantManager;
import me.anon.model.Action;
import me.anon.model.Additive;
import me.anon.model.EmptyAction;
import me.anon.model.Plant;
import me.anon.model.PlantMedium;
import me.anon.model.PlantStage;
import me.anon.model.StageChange;
import me.anon.model.Water;

/**
 * @author 7LPdWcaW
 * @project GrowTracker
 */
@Views.Injectable
public class StatisticsFragment extends Fragment
{
	private int plantIndex = -1;
	private Plant plant;

	/**
	 * @param plantIndex If -1, assume new plant
	 * @return Instantiated details fragment
	 */
	public static StatisticsFragment newInstance(int plantIndex)
	{
		Bundle args = new Bundle();
		args.putInt("plant_index", plantIndex);

		StatisticsFragment fragment = new StatisticsFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Views.InjectView(R.id.additives) private LineChart additives;
	@Views.InjectView(R.id.input_ph) private LineChart inputPh;
	@Views.InjectView(R.id.ppm) private LineChart ppm;
	@Views.InjectView(R.id.temp) private LineChart temp;

	@Views.InjectView(R.id.grow_time) private TextView growTime;
	@Views.InjectView(R.id.water_count) private TextView waterCount;
	@Views.InjectView(R.id.flush_count) private TextView flushCount;

	@Views.InjectView(R.id.germ_time) private TextView germTime;
	@Views.InjectView(R.id.germ_time_container) private View germTimeContainer;
	@Views.InjectView(R.id.veg_time) private TextView vegTime;
	@Views.InjectView(R.id.veg_time_container) private View vegTimeContainer;
	@Views.InjectView(R.id.cutting_time) private TextView cuttingTime;
	@Views.InjectView(R.id.cutting_time_container) private View cuttingTimeContainer;
	@Views.InjectView(R.id.flower_time) private TextView flowerTime;
	@Views.InjectView(R.id.flower_time_container) private View flowerTimeContainer;
	@Views.InjectView(R.id.dry_time) private TextView dryTime;
	@Views.InjectView(R.id.dry_time_container) private View dryTimeContainer;
	@Views.InjectView(R.id.cure_time) private TextView cureTime;
	@Views.InjectView(R.id.cure_time_container) private View cureTimeContainer;

	@Views.InjectView(R.id.ave_water) private TextView aveWater;

	@Views.InjectView(R.id.min_input_ph) private TextView minInputPh;
	@Views.InjectView(R.id.max_input_ph) private TextView maxInputPh;
	@Views.InjectView(R.id.ave_input_ph) private TextView aveInputPh;

	@Views.InjectView(R.id.min_ppm) private TextView minppm;
	@Views.InjectView(R.id.max_ppm) private TextView maxppm;
	@Views.InjectView(R.id.ave_ppm) private TextView aveppm;

	@Views.InjectView(R.id.temp_container) private View tempContainer;
	@Views.InjectView(R.id.min_temp) private TextView mintemp;
	@Views.InjectView(R.id.max_temp) private TextView maxtemp;
	@Views.InjectView(R.id.ave_temp) private TextView avetemp;

	@Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.statistics_view, container, false);
		Views.inject(this, view);

		return view;
	}

	@Override public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getActivity().setTitle("Plant statistics");

		if (getArguments() != null)
		{
			plantIndex = getArguments().getInt("plant_index");

			if (plantIndex > -1)
			{
				plant = PlantManager.getInstance().getPlants().get(plantIndex);
				getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			}
		}

		setStatistics();

		String[] inputAdditional = new String[3];
		StatsHelper.setInputData(plant, inputPh, inputAdditional);
		minInputPh.setText(inputAdditional[0].equals(String.valueOf(Float.MAX_VALUE)) ? "0" : inputAdditional[0]);
		maxInputPh.setText(inputAdditional[1].equals(String.valueOf(Float.MIN_VALUE)) ? "0" : inputAdditional[1]);
		aveInputPh.setText(inputAdditional[2]);

		String[] ppmAdditional = new String[3];
		StatsHelper.setPpmData(plant, ppm, ppmAdditional);
		minppm.setText(ppmAdditional[0].equals(String.valueOf(Long.MAX_VALUE)) ? "0" : ppmAdditional[0]);
		maxppm.setText(ppmAdditional[1].equals(String.valueOf(Long.MIN_VALUE)) ? "0" : ppmAdditional[1]);
		aveppm.setText(ppmAdditional[2]);

		setAdditiveStats();

		if (plant.getMedium() == PlantMedium.HYDRO)
		{
			tempContainer.setVisibility(View.VISIBLE);

			String[] tempAdditional = new String[3];
			StatsHelper.setTempData(plant, temp, tempAdditional);
			minppm.setText(tempAdditional[0]);
			maxppm.setText(tempAdditional[1]);
			aveppm.setText(tempAdditional[2]);
		}
	}

	private void setAdditiveStats()
	{
		ArrayList<Action> actions = plant.getActions();
		ArrayList<Pair<String, ArrayList<Entry>>> vals = new ArrayList<>();
		ArrayList<String> xVals = new ArrayList<>();
		Set<String> additiveNames = new HashSet<>();
		LineData data = new LineData();
		LinkedHashMap<PlantStage, Action> stageTimes = plant.getStages();

		for (Action action : actions)
		{
			if (action instanceof Water)
			{
				List<Additive> actionAdditives = ((Water)action).getAdditives();
				for (Additive additive : actionAdditives)
				{
					additiveNames.add(additive.getDescription());
				}

				PlantStage stage = null;
				long changeDate = 0;
				ListIterator<PlantStage> iterator = new ArrayList(stageTimes.keySet()).listIterator(stageTimes.size());
				while (iterator.hasPrevious())
				{
					PlantStage key = iterator.previous();
					Action changeAction = stageTimes.get(key);
					if (action.getDate() > changeAction.getDate())
					{
						stage = key;
						changeDate = changeAction.getDate();
					}
				}

				long difference = action.getDate() - changeDate;
				if (stage != null)
				{
					xVals.add(((int)TimeHelper.toDays(difference) + "" + stage.getPrintString().charAt(0)).toLowerCase());
				}
				else
				{
					xVals.add("");
				}
			}
		}

		ArrayList<LineDataSet> dataSets = new ArrayList<>();
		for (String additiveName : additiveNames)
		{
			int index = 0;
			ArrayList<Entry> additiveValues = new ArrayList<>();
			for (Action action : actions)
			{
				if (action instanceof Water)
				{
					boolean found = false;
					for (Additive additive : ((Water)action).getAdditives())
					{
						if (additiveName.equals(additive.getDescription()))
						{
							found = true;
							additiveValues.add(new Entry(additive.getAmount().floatValue(), index));
						}
					}
				}

				index++;
			}

			LineDataSet dataSet = new LineDataSet(additiveValues, additiveName);
			dataSet.setDrawCubic(true);
			dataSet.setLineWidth(1.5f);
			dataSet.setDrawCircleHole(false);

			String[] colours = getResources().getStringArray(R.array.stats_colours);
			dataSet.setColor(dataSets.size() < colours.length ? Color.parseColor(colours[dataSets.size()]) : (additiveName.hashCode() + new Random().nextInt(0xffffff)));

			dataSet.setValueTextColor(0xffffffff);
			dataSet.setCircleSize(2.0f);
			dataSet.setValueTextSize(8.0f);
			dataSet.setValueFormatter(StatsHelper.formatter);
			dataSets.add(dataSet);
		}

		LineData lineData = new LineData(xVals, dataSets);
		lineData.setValueFormatter(StatsHelper.formatter);
		lineData.setValueTextColor(0xffffffff);

		additives.getLegend().setTextSize(14.0f);
		additives.getLegend().setTextColor(0xffffffff);
		additives.getLegend().setWordWrapEnabled(true);
		additives.setBackgroundColor(0xFF0F364D);
		additives.setGridBackgroundColor(0xFF0F364D);
		additives.setDrawGridBackground(false);
		additives.setHighlightPerTapEnabled(false);
		additives.setHighlightPerDragEnabled(false);
		additives.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
		additives.getXAxis().setTextColor(0xffffffff);
		additives.getXAxis().setTextSize(14f);
		additives.getAxisRight().setEnabled(false);
		additives.getAxisLeft().setTextColor(0xffffffff);
		additives.getAxisLeft().setValueFormatter(new YAxisValueFormatter()
		{
			@Override public String getFormattedValue(float value, YAxis yAxis)
			{
				return String.format("%.2f", value);
			}
		});
		additives.getAxisLeft().setStartAtZero(false);
		additives.setScaleYEnabled(false);
		additives.setDescription("");
		additives.setPinchZoom(true);
		additives.setDoubleTapToZoomEnabled(true);
		additives.setData(lineData);
	}

	private void setStatistics()
	{
		long startDate = plant.getPlantDate();
		long endDate = System.currentTimeMillis();
		long feedDifference = 0L;
		long waterDifference = 0L;
		long lastWater = 0L;
		int totalWater = 0, totalFlush = 0;

		for (Action action : plant.getActions())
		{
			if (action instanceof StageChange)
			{
				if (((StageChange)action).getNewStage() == PlantStage.HARVESTED)
				{
					endDate = action.getDate();
				}
			}

			if (action.getClass() == Water.class)
			{
				if (lastWater != 0)
				{
					waterDifference += Math.abs(action.getDate() - lastWater);
				}

				totalWater++;
				lastWater = action.getDate();
			}

			if (action instanceof EmptyAction && ((EmptyAction)action).getAction() == Action.ActionName.FLUSH)
			{
				totalFlush++;
			}
		}

		long seconds = ((endDate - startDate) / 1000);
		double days = (double)seconds * 0.0000115741d;

		growTime.setText(String.format("%1$,.2f", days) + " days");
		waterCount.setText(String.valueOf(totalWater));
		flushCount.setText(String.valueOf(totalFlush));
		aveWater.setText(String.format("%1$,.2f", (TimeHelper.toDays(waterDifference) / (double)totalWater)) + " days");

		SortedMap<PlantStage, Long> stages = plant.calculateStageTime();

		if (stages.containsKey(PlantStage.GERMINATION))
		{
			germTime.setText((int)TimeHelper.toDays(stages.get(PlantStage.GERMINATION)) + " days");
			germTimeContainer.setVisibility(View.VISIBLE);
		}

		if (stages.containsKey(PlantStage.VEGETATION))
		{
			vegTime.setText((int)TimeHelper.toDays(stages.get(PlantStage.VEGETATION)) + " days");
			vegTimeContainer.setVisibility(View.VISIBLE);
		}

		if (stages.containsKey(PlantStage.CUTTING))
		{
			cuttingTime.setText((int)TimeHelper.toDays(stages.get(PlantStage.CUTTING)) + " days");
			cuttingTimeContainer.setVisibility(View.VISIBLE);
		}

		if (stages.containsKey(PlantStage.FLOWER))
		{
			flowerTime.setText((int)TimeHelper.toDays(stages.get(PlantStage.FLOWER)) + " days");
			flowerTimeContainer.setVisibility(View.VISIBLE);
		}

		if (stages.containsKey(PlantStage.DRYING))
		{
			dryTime.setText((int)TimeHelper.toDays(stages.get(PlantStage.DRYING)) + " days");
			dryTimeContainer.setVisibility(View.VISIBLE);
		}

		if (stages.containsKey(PlantStage.CURING))
		{
			cureTime.setText((int)TimeHelper.toDays(stages.get(PlantStage.CURING)) + " days");
			cureTimeContainer.setVisibility(View.VISIBLE);
		}
	}
}
