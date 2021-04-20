import React from "react";
import ReactDOM from "react-dom";
import { useForm } from "react-hook-form";
import "./form.css";
import App from './App';

function App() {
  const { register, handleSubmit } = useForm({
    defaultValues: {
      activeSamplingRate: 10,
      activeTestsIntensity: 100,
      activeTestsType: "TCP",
      passiveSamplingRate: 5,
      pcapMaxPacketLength: 65536,
      pcapTimeout: 10,
      pcapSessionBreak: 1000,
      interfaceName: "eth0"
    }
  });
  const onSubmit = (data) => {
    alert(JSON.stringify(data));
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <h1> Active Tests </h1>
      <label htmlFor="activeSamplingRate">
        How often perform active ping test [minutes]:
      </label>
      <input
        placeholder="10"
        type="number"
        {...register("activeSamplingRate")}
      />

      <label htmlFor="activeTestsIntensity">
        How many packets shoud active ping send [packets]:
      </label>
      <input
        placeholder="10"
        type="number"
        {...register("activeTestsIntensity")}
      />

      <label htmlFor="activeTestsType">
        Active ping should use TCP connection (if not use ICMP):
      </label>
      <input type="checkbox" {...register("activeTestsType")} />

      <h1> Passive Tests </h1>
      <label htmlFor="passiveSamplingRate">
        How often perform passive test [minutes]:
      </label>
      <input
        placeholder="5"
        type="number"
        {...register("passiveSamplingRate")}
      />

      <h1> PCAP </h1>
      <label htmlFor="pcapMaxPacketLength">
        Maximum packet length for pcap [bits]:
      </label>
      <input
        placeholder="65536"
        type="number"
        {...register("pcapMaxPacketLength")}
      />
      <label htmlFor="pcapTimeout">Timeout for pcap [seconds]:</label>
      <input placeholder="10" type="number" {...register("pcapTimeout")} />

      <label htmlFor="pcapSessionBreak">Break pcap session after [ms]:</label>
      <input
        placeholder="1000"
        type="number"
        {...register("pcapSessionBreak")}
      />

      <h1> Other </h1>
      <label htmlFor="interfaceName">Name of network interface to use:</label>
      <input placeholder="eth0" {...register("interfaceName")} />

      <input type="submit" />
    </form>
  );
}

const rootElement = document.getElementById("root");
ReactDOM.render(<App />, rootElement);
